package com.gymcrm.service;

import com.gymcrm.domain.Trainer;

import java.util.Optional;

public interface TrainerService {
    Trainer createProfile(Trainer trainer);

    Trainer updateProfile(Trainer trainer);

    Optional<Trainer> selectProfileById(Long id);
}
