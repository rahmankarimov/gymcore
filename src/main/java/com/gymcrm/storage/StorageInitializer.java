package com.gymcrm.storage;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.util.PasswordGenerator;
import com.gymcrm.util.UsernameGenerator;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StorageInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageInitializer.class);

    private InMemoryStorage storage;
    private UsernameGenerator usernameGenerator;
    private PasswordGenerator passwordGenerator;
    private ResourceLoader resourceLoader;

    @Value("${storage.initial-data}")
    private String initialDataPath;

    @PostConstruct
    public void initialize() {
        Resource resource = resourceLoader.getResource("classpath:" + initialDataPath);
        if (!resource.exists()) {
            LOGGER.warn("Initial data file was not found: {}", initialDataPath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .forEach(this::parseLine);
            LOGGER.info("Storage initialized from file: {}", initialDataPath);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed to initialize storage from " + initialDataPath, e);
        }
    }

    private void parseLine(String line) {
        String[] parts = line.split(";");
        switch (parts[0]) {
            case "TRAINEE" -> parseTrainee(parts);
            case "TRAINER" -> parseTrainer(parts);
            case "TRAINING" -> parseTraining(parts);
            case "TRAINING_TYPE" -> parseTrainingType(parts);
            default -> throw new IllegalArgumentException("Unknown record type: " + parts[0]);
        }
    }

    private void parseTrainee(String[] parts) {
        requireLength(parts, 7);
        Long id = Long.parseLong(parts[1]);
        String firstName = parts[2];
        String lastName = parts[3];
        Trainee trainee = new Trainee(id, firstName, lastName, generateUsername(firstName, lastName),
                passwordGenerator.generate(), Boolean.parseBoolean(parts[6]),
                LocalDate.parse(parts[4]), parts[5]);
        storage.getTrainees().put(id, trainee);
        storage.syncTraineeId(id);
        LOGGER.info("Initialized trainee with id {}", id);
    }

    private void parseTrainer(String[] parts) {
        requireLength(parts, 6);
        Long id = Long.parseLong(parts[1]);
        String firstName = parts[2];
        String lastName = parts[3];
        Trainer trainer = new Trainer(id, firstName, lastName, generateUsername(firstName, lastName),
                passwordGenerator.generate(), Boolean.parseBoolean(parts[5]), parts[4]);
        storage.getTrainers().put(id, trainer);
        storage.syncTrainerId(id);
        storage.findOrCreateTrainingType(parts[4]);
        LOGGER.info("Initialized trainer with id {}", id);
    }

    private void parseTraining(String[] parts) {
        requireLength(parts, 8);
        Long id = Long.parseLong(parts[1]);
        Training training = new Training(id, Long.parseLong(parts[2]), Long.parseLong(parts[3]), parts[4],
                parts[5], LocalDate.parse(parts[6]), Integer.parseInt(parts[7]));
        storage.getTrainings().put(id, training);
        storage.syncTrainingId(id);
        storage.findOrCreateTrainingType(parts[5]);
        LOGGER.info("Initialized training with id {}", id);
    }

    private void parseTrainingType(String[] parts) {
        requireLength(parts, 3);
        Long id = Long.parseLong(parts[1]);
        storage.getTrainingTypes().put(id, new com.gymcrm.domain.TrainingType(id, parts[2]));
        storage.syncTrainingTypeId(id);
        LOGGER.info("Initialized training type with id {}", id);
    }

    private String generateUsername(String firstName, String lastName) {
        Set<String> usernames = Stream.concat(
                        storage.getTrainees().values().stream().map(Trainee::getUsername),
                        storage.getTrainers().values().stream().map(Trainer::getUsername))
                .collect(Collectors.toSet());
        return usernameGenerator.generate(firstName, lastName, usernames);
    }

    private void requireLength(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Invalid record length for " + parts[0]);
        }
    }

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
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
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
