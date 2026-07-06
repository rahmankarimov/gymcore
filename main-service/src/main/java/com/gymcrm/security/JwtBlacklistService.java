package com.gymcrm.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtBlacklistService {
    private final Map<String, Instant> invalidTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiration) {
        invalidTokens.put(token, expiration);
    }

    public boolean isBlacklisted(String token) {
        Instant expiration = invalidTokens.get(token);
        if (expiration == null) {
            return false;
        }
        if (Instant.now().isAfter(expiration)) {
            invalidTokens.remove(token);
            return false;
        }
        return true;
    }
}
