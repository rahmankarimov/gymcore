package com.gymcrm.service;

import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(AppConfig.class)
class TrainingServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void shouldCreateAndFindTraining() {
        Trainee trainee = traineeService.createProfile(new Trainee(null, "Sara", "Mammadova", null, null,
                true, LocalDate.of(2001, 3, 4), "Baku"));
        Trainer trainer = trainerService.createProfile(new Trainer(null, "Orkhan", "Karimov", null, null,
                true, "Fitness"));

        Training created = trainingService.createProfile(new Training(null, trainee.getId(), trainer.getId(),
                "Morning Training", "Fitness", LocalDate.of(2026, 5, 1), 60));

        assertEquals(1L, created.getId());
        assertEquals("Morning Training", trainingService.selectProfileById(created.getId()).orElseThrow().getTrainingName());
    }
}
