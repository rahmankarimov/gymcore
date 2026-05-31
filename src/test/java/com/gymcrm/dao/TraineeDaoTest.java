package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.TestDatabaseCleaner;
import com.gymcrm.domain.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TraineeDaoTest {

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private TestDatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.clean();
    }

    @Test
    void shouldSaveFindUpdateAndDeleteTrainee() {
        Trainee trainee = new Trainee(null, "Ali", "Valiyev", "Ali.Valiyev", "password123",
                true, LocalDate.of(2000, 2, 2), "Baku");

        Trainee saved = traineeDao.save(trainee);
        assertEquals(1L, saved.getId());
        assertTrue(traineeDao.findById(saved.getId()).isPresent());

        saved.setAddress("Ganja");
        traineeDao.update(saved);
        assertEquals("Ganja", traineeDao.findById(saved.getId()).orElseThrow().getAddress());

        traineeDao.delete(saved.getId());
        assertFalse(traineeDao.findById(saved.getId()).isPresent());
    }
}
