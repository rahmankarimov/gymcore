package com.gymcrm.service;

import com.gymcrm.domain.Training;
import com.gymcrm.domain.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingService {
    Training createProfile(Training training);

    Training createProfile(String traineeUsername, String traineePassword,
                           String trainerUsername, String trainerPassword,
                           Training training);

    Training createProfileForAuthenticatedUser(String authenticatedUsername, String password,
                                               String traineeUsername, String trainerUsername,
                                               Training training);

    Optional<Training> selectProfileById(Long id);

    List<TrainingType> getTrainingTypes();
}
