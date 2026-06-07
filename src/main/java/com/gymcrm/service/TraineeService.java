package com.gymcrm.service;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TraineeService {
    Trainee createProfile(Trainee trainee);

    Trainee updateProfile(Trainee trainee);

    void deleteProfile(Long id);

    Optional<Trainee> selectProfileById(Long id);

    Optional<Trainee> selectProfileByUsername(String username);

    boolean authenticate(String username, String password);

    void changePassword(String username, String newPassword);

    void changeActiveState(String username, boolean active);

    void deleteProfileByUsername(String username);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                String trainerName, String trainingType);

    List<Trainer> getUnassignedTrainers(String username);

    Trainee updateTrainers(String username, Set<String> trainerUsernames);
}
