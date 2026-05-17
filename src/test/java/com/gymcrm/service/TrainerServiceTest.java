package com.gymcrm.service;

import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(AppConfig.class)
class TrainerServiceTest {

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void shouldCreateUpdateAndFindTrainer() {
        Trainer created = trainerService.createProfile(new Trainer(null, "Mike", "Brown", null, null,
                true, "Fitness"));

        assertNotNull(created.getId());
        assertEquals("Mike.Brown", created.getUsername());
        assertEquals(10, created.getPassword().length());
        assertEquals("Fitness", trainerService.selectProfileById(created.getId()).orElseThrow().getSpecialization());

        created.setSpecialization("Boxing");
        Trainer updated = trainerService.updateProfile(created);
        assertEquals("Boxing", updated.getSpecialization());
        assertEquals("Mike.Brown", updated.getUsername());
    }
}
