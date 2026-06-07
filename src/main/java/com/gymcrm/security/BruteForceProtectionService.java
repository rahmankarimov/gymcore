package com.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BruteForceProtectionService {
    private static final int MAX_FAILURES = 3;
    private static final int BLOCK_MINUTES = 5;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();
    private final Clock clock = Clock.systemUTC();

    public boolean isBlocked(String username) {
        LoginAttempt attempt = attempts.get(username);
        if (attempt == null || attempt.blockedUntil == null) {
            return false;
        }
        if (Instant.now(clock).isAfter(attempt.blockedUntil)) {
            attempts.remove(username);
            return false;
        }
        return true;
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
    }

    public void loginFailed(String username) {
        attempts.compute(username, (key, current) -> {
            int failures = current == null ? 1 : current.failures + 1;
            Instant blockedUntil = failures >= MAX_FAILURES
                    ? Instant.now(clock).plus(BLOCK_MINUTES, ChronoUnit.MINUTES)
                    : null;
            return new LoginAttempt(failures, blockedUntil);
        });
    }

    private record LoginAttempt(int failures, Instant blockedUntil) {
    }
}
