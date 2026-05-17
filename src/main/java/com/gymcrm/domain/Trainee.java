package com.gymcrm.domain;

import java.time.LocalDate;

public class Trainee extends User {
    private Long id;
    private LocalDate dateOfBirth;
    private String address;

    public Trainee() {
    }

    public Trainee(Long id, String firstName, String lastName, String username, String password,
                   boolean active, LocalDate dateOfBirth, String address) {
        super(firstName, lastName, username, password, active);
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Trainee{id=" + id + ", username='" + getUsername() + "'}";
    }
}
