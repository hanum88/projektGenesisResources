package org.example.model;
import java.util.UUID;

public class User {
    private long id;
    private String name;
    private String surname;
    private String personId;
    private String uuid;

    public User(String name, String surname, String personId) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
        setUuid(UUID.randomUUID().toString());
    }

    //konstruktor jen pro uskladnění id pro deleteUser službu, ať můžu přepoužít službu na kontrolu existence,
    //která má na vstupu objekt User
    public User(long id) {
        this.id = id;
    }

    public User() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
