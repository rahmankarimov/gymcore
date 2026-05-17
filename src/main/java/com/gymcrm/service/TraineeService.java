package com.gymcrm.service;

import com.gymcrm.domain.Trainee;

import java.util.Optional;

public interface TraineeService {
    Trainee createProfile(Trainee trainee);

    Trainee updateProfile(Trainee trainee);

    void deleteProfile(Long id);

    Optional<Trainee> selectProfileById(Long id);
}
