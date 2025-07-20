package org.example.service;

import org.example.dto.UserDetailAdvancedDto;
import org.example.dto.UserDetailBasicDto;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@Service
public class UsersService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public ResponseEntity<?> getAllUsers(boolean detail) {
        List<User> result = jdbcTemplate.query("select * from Users", new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet result, int rowNum) throws SQLException {
                User user = new User();
                user.setId(result.getLong("ID"));
                user.setName(result.getString("Name"));
                user.setSurname(result.getString("Surname"));
                user.setPersonId(result.getString("PersonId"));
                user.setUuid(result.getString("Uuid"));
                return user;
            }
        });


        if (detail) {
            List<UserDetailAdvancedDto> allUsersDetailAdvanced = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                allUsersDetailAdvanced.add(new UserDetailAdvancedDto(result.get(i).getId(), result.get(i).getName(),
                        result.get(i).getSurname(), result.get(i).getPersonId(), result.get(i).getUuid()));
            }
            return new ResponseEntity<>(allUsersDetailAdvanced, HttpStatus.OK);

        } else {
            List<UserDetailBasicDto> allUsersDetailBasic = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                allUsersDetailBasic.add(new UserDetailBasicDto(result.get(i).getId(), result.get(i).getName(),
                        result.get(i).getSurname()));
            }
            return new ResponseEntity<>(allUsersDetailBasic, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> addUser(User user) {
        if (user.getPersonId().length() == 12) {
            if (userExistsByIdentifier(user, "personID")) {
                return new ResponseEntity("uživatel se zadaným PersonID již existuje", HttpStatus.BAD_REQUEST);
            } else if (!personIdExistsInList(user.getPersonId())) {
                return new ResponseEntity("Zadané PersonID není v seznamu povolených PersonID", HttpStatus.BAD_REQUEST);
            } else {
                //musím založit userNew aby se mi vygenerovalo UUID
                User userNew = new User(user.getName(), user.getSurname(), user.getPersonId());
                //pak hodnoty vč. UUID nahážu do databáze kde se teprve vytvoří ID
                jdbcTemplate.update("insert into Users (Name, Surname, PersonID, Uuid) values(?,?,?,?)",
                        userNew.getName(), userNew.getSurname(), userNew.getPersonId(), userNew.getUuid());
                //pak si dotáhnu záznam z databáze podle PersonID abych mohla poslat ID (které do té doby neznám) do metody userDetail
                //a to celé proto aby se mi vracel do postmana kompletní detailní výstup (aby se nevracely 4 hodnoty a ID=null)
                Long id = jdbcTemplate.queryForObject("select ID from Users where Personid = ?", Long.class, userNew.getPersonId());
                return new ResponseEntity(userDetail(id, true), HttpStatus.OK);
            }
        } else {
            return new ResponseEntity("Zadané PersonID je ve špatném formátu", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean personIdExistsInList(String personId) {
        List<String> personIdList = new ArrayList<>();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dataPersonId");
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            personIdList.add(scanner.nextLine());
        }
        return personIdList.contains(personId) ? true : false;
    }

    private boolean userExistsByIdentifier(User user, String identifier) {
        String queryString;
        Object queryParameter;
        switch (identifier.toLowerCase()) {
            case "personid":
                queryString = "select count(*) from Users where personid = ?";
                queryParameter = user.getPersonId();
                break;
            case "id":
                queryString = "select count(*) from Users where id = ?";
                queryParameter = user.getId();
                break;
            case "uuid":
                queryString = "select count(*) from Users where uuid = ?";
                queryParameter = user.getUuid();
                break;
            default:
                throw new IllegalArgumentException("neplatný identifikátor " + identifier);
        }
        Integer count = jdbcTemplate.queryForObject(queryString, Integer.class, queryParameter);
        return count != null && count > 0;
    }

    public ResponseEntity<?> userDetail(long id, boolean detail) {
        String query = "select * from Users where id=" + id;
        List<User> result = jdbcTemplate.query(query, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet result, int rowNum) throws SQLException {
                User user = new User();
                user.setId(result.getLong("ID"));
                user.setName(result.getString("Name"));
                user.setSurname(result.getString("Surname"));
                user.setPersonId(result.getString("PersonID"));
                user.setUuid(result.getString("Uuid"));
                return user;
            }
        });

        if (result.isEmpty()) {
            return new ResponseEntity<>("Uživatel nenalezen", HttpStatus.NOT_FOUND);
        } else {
            User user = result.get(0);
            if (detail) {
                return new ResponseEntity<>(new UserDetailAdvancedDto(user.getId(), user.getName(), user.getSurname(),
                        user.getPersonId(), user.getUuid()), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new UserDetailBasicDto(user.getId(), user.getName(), user.getSurname()), HttpStatus.OK);
            }
        }
    }

    public ResponseEntity<?> editUser(User user) {
        if (userExistsByIdentifier(user, "id")) {
            String queryString = "update Users set Name = ?, surname = ? where id = ?";
            jdbcTemplate.update(queryString, user.getName(), user.getSurname(), user.getId());
            return new ResponseEntity(new UserDetailBasicDto(user.getId(), user.getName(), user.getSurname()), HttpStatus.OK);
        } else {
            return new ResponseEntity("Uživatel se zadaným ID neexistuje.", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> deleteUser(long id) {
        if (userExistsByIdentifier(new User(id), "id")) {
            String queryString = "delete from Users where id = ?";
            jdbcTemplate.update(queryString, id);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity("Uživatel se zadaným ID neexistuje. ", HttpStatus.BAD_REQUEST);
        }
    }
}
