package com.gymcrm.workload.repository;

import com.gymcrm.workload.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {
    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);
}
