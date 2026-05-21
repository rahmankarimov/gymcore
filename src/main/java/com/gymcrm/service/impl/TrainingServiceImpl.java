package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import com.gymcrm.domain.Training;
import com.gymcrm.service.TrainingService;
import com.gymcrm.exception.EntityNotFoundException;
import com.gymcrm.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Override
    @Transactional
    public Training createProfile(Training training) {
        LOGGER.info("Creating training profile named {}", training.getTrainingName());
        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found: " + training.getTraineeId());
        }
        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            throw new EntityNotFoundException("Trainer not found: " + training.getTrainerId());
        }
        if (training.getTrainingDuration() <= 0) {
            throw new ValidationException("Training duration must be positive");
        }
        return trainingDao.save(training);
    }

    @Override
    @Transactional
    public Training createProfile(String traineeUsername, String traineePassword,
                                  String trainerUsername, String trainerPassword,
                                  Training training) {
        if (!traineeDao.credentialsMatch(traineeUsername, traineePassword)
                || !trainerDao.credentialsMatch(trainerUsername, trainerPassword)) {
            throw new SecurityException("Invalid trainee or trainer credentials");
        }
        training.setTraineeId(traineeDao.findByUsername(traineeUsername).orElseThrow().getId());
        training.setTrainerId(trainerDao.findByUsername(trainerUsername).orElseThrow().getId());
        return createProfile(training);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Training> selectProfileById(Long id) {
        LOGGER.info("Selecting training profile with id {}", id);
        return trainingDao.findById(id);
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }
}
