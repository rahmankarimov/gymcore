package com.gymcrm.workload.dto;

import java.util.List;

public record TrainerSummaryResponse(String trainerUsername,
                                     String trainerFirstName,
                                     String trainerLastName,
                                     boolean active,
                                     List<YearSummaryResponse> years) {
}
