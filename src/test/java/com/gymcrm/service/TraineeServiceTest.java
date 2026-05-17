package com.gymcrm.service;

import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class TraineeServiceTest {

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void shouldCreateUpdateFindAndDeleteTrainee() {
        Trainee created = traineeService.createProfile(new Trainee(null, "John", "Smith", null, null,
                true, LocalDate.of(1999, 1, 1), "Baku"));

        assertNotNull(created.getId());
        assertEquals("John.Smith", created.getUsername());
        assertEquals(10, created.getPassword().length());
        assertEquals("Baku", traineeService.selectProfileById(created.getId()).orElseThrow().getAddress());

        created.setAddress("Sumgait");
        Trainee updated = traineeService.updateProfile(created);
        assertEquals("Sumgait", updated.getAddress());
        assertEquals("John.Smith", updated.getUsername());

        traineeService.deleteProfile(created.getId());
        assertFalse(traineeService.selectProfileById(created.getId()).isPresent());
    }

    @Test
    void shouldGenerateDuplicateUsernameSuffixAcrossTraineesAndTrainers() {
        trainerService.createProfile(new Trainer(null, "John", "Smith", null, null, true, "Fitness"));
        traineeService.createProfile(new Trainee(null, "John", "Smith", null, null,
                true, LocalDate.of(2000, 1, 1), "Baku"));

        Trainee secondDuplicate = traineeService.createProfile(new Trainee(null, "John", "Smith", null, null,
                true, LocalDate.of(2001, 1, 1), "Baku"));

        assertEquals("John.Smith2", secondDuplicate.getUsername());
    }
}
