package com.gymcrm.dao;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TraineeDao {
    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    boolean credentialsMatch(String username, String password);

    void changePassword(String username, String password);

    void setActive(String username, boolean active);

    void deleteByUsername(String username);

    List<Trainer> findUnassignedTrainers(String username);

    Trainee updateTrainers(String username, Set<String> trainerUsernames);

    List<Trainee> findAll();
}
