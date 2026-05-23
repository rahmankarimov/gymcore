package com.gymcrm.dao;

import com.gymcrm.domain.Training;
import com.gymcrm.domain.TrainingType;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TrainingDao {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findByTraineeCriteria(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                         String trainerName, String trainingType);

    List<Training> findByTrainerCriteria(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                         String traineeName);

    List<Training> findAll();

    List<TrainingType> findTrainingTypes();
}
