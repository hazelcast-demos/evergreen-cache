package org.hazelcast.evergreencache;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class Person {

    private Long id;

    private String firstName;
    private String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public String getFirstName() {
        return firstName;
    }

    public Long getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}