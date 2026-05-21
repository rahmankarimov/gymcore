package com.gymcrm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
public class Trainer extends User {
    private String specialization;

    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    @OneToMany(mappedBy = "trainer")
    private Set<Training> trainings = new HashSet<>();

    public Trainer() {
    }

    public Trainer(Long id, String firstName, String lastName, String username, String password,
                   boolean active, String specialization) {
        super(firstName, lastName, username, password, active);
        setId(id);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    @Override
    public String toString() {
        return "Trainer{id=" + getId() + ", username='" + getUsername() + "'}";
    }
}
