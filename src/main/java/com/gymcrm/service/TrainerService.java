package com.gymcrm.service;

import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerService {
    Trainer createProfile(Trainer trainer);

    Trainer updateProfile(Trainer trainer);

    Optional<Trainer> selectProfileById(Long id);

    Optional<Trainer> selectProfileByUsername(String username);

    boolean authenticate(String username, String password);

    void changePassword(String username, String newPassword);

    void changeActiveState(String username, boolean active);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                String traineeName);
}
