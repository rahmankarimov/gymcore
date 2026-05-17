package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.service.TrainerService;
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
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;

    @Override
    public Trainer createProfile(Trainer trainer) {
        LOGGER.info("Creating trainer profile for {} {}", trainer.getFirstName(), trainer.getLastName());
        trainer.setUsername(usernameGenerator.generate(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames()));
        trainer.setPassword(passwordGenerator.generate());
        return trainerDao.save(trainer);
    }

    @Override
    public Trainer updateProfile(Trainer trainer) {
        LOGGER.info("Updating trainer profile with id {}", trainer.getId());
        Trainer existing = trainerDao.findById(trainer.getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found: " + trainer.getId()));
        trainer.setUsername(existing.getUsername());
        trainer.setPassword(existing.getPassword());
        return trainerDao.update(trainer);
    }

    @Override
    public Optional<Trainer> selectProfileById(Long id) {
        LOGGER.info("Selecting trainer profile with id {}", id);
        return trainerDao.findById(id);
    }

    private Set<String> existingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername))
                .collect(Collectors.toSet());
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
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
