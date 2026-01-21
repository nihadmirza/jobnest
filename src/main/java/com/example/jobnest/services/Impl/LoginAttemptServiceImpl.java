package com.example.jobnest.services.impl;

import com.example.jobnest.services.LoginAttemptService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPT = 5;
    // Simple cache: IP -> attempts. For production, consider Guava or Caffeine with
    // automatic expiration.
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();
    private static final long LOCK_DURATION_MS = 15L * 60 * 1000; // 15 minutes

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockTimeCache.remove(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);

        if (attempts >= MAX_ATTEMPT) {
            lockTimeCache.put(key, System.currentTimeMillis());
        }
    }

    public boolean isBlocked(String key) {
        if (!lockTimeCache.containsKey(key)) {
            return false;
        }

        long lockTime = lockTimeCache.get(key);
        if (System.currentTimeMillis() - lockTime > LOCK_DURATION_MS) {
            // Lock expired
            lockTimeCache.remove(key);
            attemptsCache.remove(key);
            return false;
        }

        return true;
    }
}
