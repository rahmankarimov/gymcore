package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.PasswordGenerator;
import com.gymcrm.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Override
    public Trainee createProfile(Trainee trainee) {
        LOGGER.info("Creating trainee profile for {} {}", trainee.getFirstName(), trainee.getLastName());
        trainee.setUsername(usernameGenerator.generate(
                trainee.getFirstName(), trainee.getLastName(), existingUsernames()));
        trainee.setPassword(passwordGenerator.generate());
        return traineeDao.save(trainee);
    }

    @Override
    public Trainee updateProfile(Trainee trainee) {
        LOGGER.info("Updating trainee profile with id {}", trainee.getId());
        Trainee existing = traineeDao.findById(trainee.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found: " + trainee.getId()));
        trainee.setUsername(existing.getUsername());
        trainee.setPassword(existing.getPassword());
        return traineeDao.update(trainee);
    }

    @Override
    public void deleteProfile(Long id) {
        LOGGER.info("Deleting trainee profile with id {}", id);
        if (traineeDao.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Trainee not found: " + id);
        }
        traineeDao.delete(id);
    }

    @Override
    public Optional<Trainee> selectProfileById(Long id) {
        LOGGER.info("Selecting trainee profile with id {}", id);
        return traineeDao.findById(id);
    }

    private Set<String> existingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername))
                .collect(Collectors.toSet());
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }
}
