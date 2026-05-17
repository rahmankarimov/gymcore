package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Training;
import com.gymcrm.storage.InMemoryStorage;
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
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void shouldSaveAndFindTraining() {
        Training training = new Training(null, 1L, 1L, "Evening Training", "Fitness",
                LocalDate.of(2026, 5, 2), 45);

        Training saved = trainingDao.save(training);

        assertEquals(1L, saved.getId());
        assertEquals("Evening Training", trainingDao.findById(saved.getId()).orElseThrow().getTrainingName());
        assertTrue(storage.getTrainingTypes().values().stream()
                .anyMatch(type -> "Fitness".equals(type.getTrainingTypeName())));
    }
}
