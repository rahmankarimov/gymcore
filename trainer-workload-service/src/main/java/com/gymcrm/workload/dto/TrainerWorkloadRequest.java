package com.gymcrm.workload.dto;

import java.time.LocalDate;

public record TrainerWorkloadRequest(String trainerUsername,
                                     String trainerFirstName,
                                     String trainerLastName,
                                     Boolean active,
                                     LocalDate trainingDate,
                                     Integer trainingDuration,
                                     ActionType actionType) {
}
