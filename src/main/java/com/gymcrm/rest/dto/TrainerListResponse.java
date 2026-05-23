package com.gymcrm.rest.dto;

import java.util.List;

public record TrainerListResponse(List<TrainerSummaryResponse> trainers) {
}
