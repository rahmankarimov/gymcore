package com.gymcrm.workload;

import java.time.LocalDate;

public record TrainerWorkloadRequest(String trainerUsername,
                                     String trainerFirstName,
                                     String trainerLastName,
                                     Boolean active,
                                     LocalDate trainingDate,
                                     Integer trainingDuration,
                                     WorkloadActionType actionType) {
}
