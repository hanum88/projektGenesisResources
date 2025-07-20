package org.example.dto;

public class UserDetailAdvancedDto {
    private Long id;
    private String name;
    private String surname;
    private String personId;
    private String uuid;

    public UserDetailAdvancedDto(Long id, String name, String surname, String personId, String uuid) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.personId = personId;
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPersonId() {
        return personId;
    }

    public String getUuid() {
        return uuid;
    }
}
