package com.gymcrm.workload.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrainerWorkload {
    private final String trainerUsername;
    private volatile String trainerFirstName;
    private volatile String trainerLastName;
    private volatile boolean active;
    private final Map<Integer, Map<Integer, Integer>> durationsByYearMonth = new ConcurrentHashMap<>();

    public TrainerWorkload(String trainerUsername, String trainerFirstName, String trainerLastName, boolean active) {
        this.trainerUsername = trainerUsername;
        this.trainerFirstName = trainerFirstName;
        this.trainerLastName = trainerLastName;
        this.active = active;
    }

    public synchronized void apply(String firstName, String lastName, boolean active, int year, int month,
                                   int durationDelta) {
        this.trainerFirstName = firstName;
        this.trainerLastName = lastName;
        this.active = active;
        Map<Integer, Integer> monthDurations = durationsByYearMonth.computeIfAbsent(
                year, ignored -> new ConcurrentHashMap<>());
        int updatedDuration = Math.max(0, monthDurations.getOrDefault(month, 0) + durationDelta);
        monthDurations.put(month, updatedDuration);
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public boolean isActive() {
        return active;
    }

    public Map<Integer, Map<Integer, Integer>> getDurationsByYearMonth() {
        return durationsByYearMonth;
    }
}
