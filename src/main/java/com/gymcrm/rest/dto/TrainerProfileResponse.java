package com.gymcrm.rest.dto;

import java.util.List;

public record TrainerProfileResponse(String username, String firstName, String lastName, String specialization,
                                     boolean active, List<TraineeSummaryResponse> trainees) {
}
