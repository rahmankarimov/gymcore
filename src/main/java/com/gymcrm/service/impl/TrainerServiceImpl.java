package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.service.TrainerService;
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
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;
    private com.gymcrm.dao.TrainingDao trainingDao;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Trainer createProfile(Trainer trainer) {
        validateUser(trainer);
        LOGGER.info("Creating trainer profile for {} {}", trainer.getFirstName(), trainer.getLastName());
        trainer.setUsername(usernameGenerator.generate(
                trainer.getFirstName(), trainer.getLastName(), existingUsernames()));
        String rawPassword = passwordGenerator.generate();
        trainer.setPassword(passwordEncoder.encode(rawPassword));
        Trainer saved = trainerDao.save(trainer);
        return new Trainer(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getUsername(),
                rawPassword, saved.isActive(), saved.getSpecialization());
    }

    @Override
    @Transactional
    public Trainer updateProfile(Trainer trainer) {
        validateUser(trainer);
        LOGGER.info("Updating trainer profile with id {}", trainer.getId());
        Trainer existing = trainerDao.findById(trainer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainer.getId()));
        trainer.setUsername(existing.getUsername());
        trainer.setPassword(existing.getPassword());
        return trainerDao.update(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectProfileById(Long id) {
        LOGGER.info("Selecting trainer profile with id {}", id);
        return trainerDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> selectProfileByUsername(String username) {
        return trainerDao.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        return trainerDao.findByUsername(username)
                .filter(trainer -> passwordEncoder.matches(password, trainer.getPassword()))
                .isPresent();
    }

    @Override
    @Transactional
    public void changePassword(String username, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new ValidationException("Password is required");
        }
        trainerDao.changePassword(username, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void changeActiveState(String username, boolean active) {
        trainerDao.setActive(username, active);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                       String traineeName) {
        return trainingDao.findByTrainerCriteria(username, fromDate, toDate, traineeName);
    }

    private Set<String> existingUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(Trainee::getUsername),
                        trainerDao.findAll().stream().map(Trainer::getUsername))
                .collect(Collectors.toSet());
    }

    private void validateUser(Trainer trainer) {
        if (trainer == null || trainer.getFirstName() == null || trainer.getFirstName().isBlank()
                || trainer.getLastName() == null || trainer.getLastName().isBlank()
                || trainer.getSpecialization() == null || trainer.getSpecialization().isBlank()) {
            throw new ValidationException("First name, last name and specialization are required");
        }
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
