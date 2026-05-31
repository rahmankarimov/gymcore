package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.TestDatabaseCleaner;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainingDaoTest {

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @Test
    void shouldSaveAndFindTraining() {
        Trainee trainee = traineeDao.save(new Trainee(null, "Ali", "Valiyev", "Ali.Valiyev", "password123",
                true, LocalDate.of(2000, 2, 2), "Baku"));
        Trainer trainer = trainerDao.save(new Trainer(null, "Leyla", "Aliyeva", "Leyla.Aliyeva", "password123",
                true, "Fitness"));
        Training training = new Training(null, trainee.getId(), trainer.getId(), "Evening Training", "Fitness",
                LocalDate.of(2026, 5, 2), 45);

        Training saved = trainingDao.save(training);

        assertEquals(1L, saved.getId());
        assertEquals("Evening Training", trainingDao.findById(saved.getId()).orElseThrow().getTrainingName());
        assertTrue(trainingDao.findAll().stream()
                .anyMatch(existing -> "Fitness".equals(existing.getTrainingType())));
    }
}
