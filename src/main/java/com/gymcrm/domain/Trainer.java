package com.gymcrm.domain;

public class Trainer extends User {
    private Long id;
    private String specialization;

    public Trainer() {
    }

    public Trainer(Long id, String firstName, String lastName, String username, String password,
                   boolean active, String specialization) {
        super(firstName, lastName, username, password, active);
        this.id = id;
        this.specialization = specialization;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Trainer{id=" + id + ", username='" + getUsername() + "'}";
    }
}
