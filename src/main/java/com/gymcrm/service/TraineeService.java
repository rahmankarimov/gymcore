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

    Optional<Trainee> selectProfileByUsername(String username, String password);

    boolean authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void changeActiveState(String username, String password, boolean active);

    void deleteProfileByUsername(String username, String password);

    List<Training> getTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                String trainerName, String trainingType);

    List<Trainer> getUnassignedTrainers(String username, String password);

    Trainee updateTrainers(String username, String password, Set<String> trainerUsernames);
}
