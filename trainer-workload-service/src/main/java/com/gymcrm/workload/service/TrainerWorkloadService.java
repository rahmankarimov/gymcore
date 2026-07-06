package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.MonthlyDurationResponse;
import com.gymcrm.workload.dto.TrainerSummaryResponse;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;

public interface TrainerWorkloadService {
    void apply(TrainerWorkloadRequest request);

    TrainerSummaryResponse getSummary(String trainerUsername);

    MonthlyDurationResponse getMonthlyDuration(String trainerUsername, int year, int month);
}
