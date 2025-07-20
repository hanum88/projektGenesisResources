package org.example.dto;

public class UserDetailBasicDto {
    private Long id;
    private String name;
    private String surname;

    public UserDetailBasicDto(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
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
}
