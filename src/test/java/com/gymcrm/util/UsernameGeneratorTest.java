package com.gymcrm.util;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorTest {

    private final UsernameGenerator usernameGenerator = new UsernameGenerator();

    @Test
    void shouldGenerateUsernameWithoutDuplicate() {
        String username = usernameGenerator.generate("John", "Smith", Set.of());

        assertEquals("John.Smith", username);
    }

    @Test
    void shouldGenerateUsernameWithDuplicateSuffix() {
        String username = usernameGenerator.generate("John", "Smith",
                Set.of("John.Smith", "John.Smith1", "John.Smith2"));

        assertEquals("John.Smith3", username);
    }
}
