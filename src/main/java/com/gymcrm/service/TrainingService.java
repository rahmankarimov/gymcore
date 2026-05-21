package com.gymcrm.service;

import com.gymcrm.domain.Training;

import java.util.Optional;

public interface TrainingService {
    Training createProfile(Training training);

    Training createProfile(String traineeUsername, String traineePassword,
                           String trainerUsername, String trainerPassword,
                           Training training);

    Optional<Training> selectProfileById(Long id);
}
