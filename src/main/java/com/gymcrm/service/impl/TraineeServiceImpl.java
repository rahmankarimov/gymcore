package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.PasswordGenerator;
import com.gymcrm.util.UsernameGenerator;
import com.gymcrm.exception.EntityNotFoundException;
import com.gymcrm.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private com.gymcrm.dao.TrainingDao trainingDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Trainee createProfile(Trainee trainee) {
        validateUser(trainee);
        LOGGER.info("Creating trainee profile for {} {}", trainee.getFirstName(), trainee.getLastName());
        trainee.setUsername(usernameGenerator.generate(
                trainee.getFirstName(), trainee.getLastName(), existingUsernames()));
        String rawPassword = passwordGenerator.generate();
        trainee.setPassword(passwordEncoder.encode(rawPassword));
        Trainee saved = traineeDao.save(trainee);
        return new Trainee(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getUsername(),
                rawPassword, saved.isActive(), saved.getDateOfBirth(), saved.getAddress());
    }

    @Override
    @Transactional
    public Trainee updateProfile(Trainee trainee) {
        validateUser(trainee);
        LOGGER.info("Updating trainee profile with id {}", trainee.getId());
        Trainee existing = traineeDao.findById(trainee.getId())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + trainee.getId()));
        trainee.setUsername(existing.getUsername());
        trainee.setPassword(existing.getPassword());
        return traineeDao.update(trainee);
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        LOGGER.info("Deleting trainee profile with id {}", id);
        if (traineeDao.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found: " + id);
        }
        traineeDao.delete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectProfileById(Long id) {
        LOGGER.info("Selecting trainee profile with id {}", id);
        return traineeDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> selectProfileByUsername(String username) {
        return traineeDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return traineeDao.findByUsername(username)
                .filter(trainee -> passwordEncoder.matches(password, trainee.getPassword()))
                .isPresent();
    }

    @Override
    @Transactional
    public void changePassword(String username, String newPassword) {
        if (isBlank(newPassword)) {
            throw new ValidationException("Password is required");
        }
        traineeDao.changePassword(username, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void changeActiveState(String username, boolean active) {
        traineeDao.setActive(username, active);
    }

    @Override
    @Transactional
    public void deleteProfileByUsername(String username) {
        traineeDao.deleteByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                       String trainerName, String trainingType) {
        return trainingDao.findByTraineeCriteria(username, fromDate, toDate, trainerName, trainingType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getUnassignedTrainers(String username) {
        return traineeDao.findUnassignedTrainers(username);
    }

    @Override
    @Transactional
    public Trainee updateTrainers(String username, Set<String> trainerUsernames) {
        return traineeDao.updateTrainers(username, trainerUsernames);
    }

    private Set<String> existingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername))
                .collect(Collectors.toSet());
    }

    private void validateUser(Trainee trainee) {
        if (trainee == null || isBlank(trainee.getFirstName()) || isBlank(trainee.getLastName())) {
            throw new ValidationException("First name and last name are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
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
    public void setTrainingDao(com.gymcrm.dao.TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setUsernameGenerator(UsernameGenerator usernameGenerator) {
        this.usernameGenerator = usernameGenerator;
    }

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}
