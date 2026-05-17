package com.gymcrm.dao;

import com.gymcrm.domain.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerDao {
    Trainer save(Trainer trainer);

    Trainer update(Trainer trainer);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();
}
