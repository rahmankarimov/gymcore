package com.gymcrm.util;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UsernameGenerator {

    public String generate(String firstName, String lastName, Set<String> existingUsernames) {
        String baseUsername = firstName.trim() + "." + lastName.trim();
        if (!existingUsernames.contains(baseUsername)) {
            return baseUsername;
        }

        int suffix = 1;
        String candidate = baseUsername + suffix;
        while (existingUsernames.contains(candidate)) {
            suffix++;
            candidate = baseUsername + suffix;
        }
        return candidate;
    }
}
