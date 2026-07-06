package com.gymcrm.workload.repository;

import com.gymcrm.workload.model.TrainerWorkload;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class TrainerWorkloadRepository {
    private final ConcurrentMap<String, TrainerWorkload> workloads = new ConcurrentHashMap<>();

    public TrainerWorkload getOrCreate(String username, String firstName, String lastName, boolean active) {
        return workloads.computeIfAbsent(username, ignored -> new TrainerWorkload(username, firstName, lastName, active));
    }

    public Optional<TrainerWorkload> findByUsername(String username) {
        return Optional.ofNullable(workloads.get(username));
    }
}
