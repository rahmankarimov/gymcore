package com.gymcrm.storage;

import com.gymcrm.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(AppConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class StorageInitializerTest {

    @Autowired
    private InMemoryStorage storage;

    @Test
    void shouldLoadInitialStorageFromFile() {
        assertEquals(1, storage.getTrainees().size());
        assertEquals(1, storage.getTrainers().size());
        assertEquals(1, storage.getTrainings().size());
        assertTrue(storage.getTrainingTypes().values().stream()
                .anyMatch(type -> "Fitness".equals(type.getTrainingTypeName())));
    }
}
