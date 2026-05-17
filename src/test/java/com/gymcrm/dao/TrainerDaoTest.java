package com.gymcrm.dao;

import com.gymcrm.config.AppConfig;
import com.gymcrm.domain.Trainer;
import com.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
class TrainerDaoTest {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private InMemoryStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void shouldSaveFindAndUpdateTrainer() {
        Trainer trainer = new Trainer(null, "Leyla", "Aliyeva", "Leyla.Aliyeva", "password123",
                true, "Yoga");

        Trainer saved = trainerDao.save(trainer);
        assertEquals(1L, saved.getId());
        assertTrue(trainerDao.findById(saved.getId()).isPresent());

        saved.setSpecialization("Fitness");
        trainerDao.update(saved);
        assertEquals("Fitness", trainerDao.findById(saved.getId()).orElseThrow().getSpecialization());
    }
}
