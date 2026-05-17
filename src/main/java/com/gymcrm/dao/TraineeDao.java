package com.gymcrm.dao;

import com.gymcrm.domain.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();
}
