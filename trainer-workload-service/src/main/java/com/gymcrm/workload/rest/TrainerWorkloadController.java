package com.gymcrm.workload.rest;

import com.gymcrm.workload.dto.MonthlyDurationResponse;
import com.gymcrm.workload.dto.TrainerSummaryResponse;
import com.gymcrm.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trainer-workloads")
public class TrainerWorkloadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadController.class);

    private final TrainerWorkloadService trainerWorkloadService;

    public TrainerWorkloadController(TrainerWorkloadService trainerWorkloadService) {
        this.trainerWorkloadService = trainerWorkloadService;
    }

    @GetMapping("/{trainerUsername}")
    public TrainerSummaryResponse getSummary(@PathVariable String trainerUsername) {
        LOGGER.info("Get trainer workload summary operation trainerUsername={}", trainerUsername);
        return trainerWorkloadService.getSummary(trainerUsername);
    }

    @GetMapping("/{trainerUsername}/years/{year}/months/{month}")
    public MonthlyDurationResponse getMonthlyDuration(@PathVariable String trainerUsername,
                                                      @PathVariable int year,
                                                      @PathVariable int month) {
        LOGGER.info("Get trainer monthly workload operation trainerUsername={} year={} month={}",
                trainerUsername, year, month);
        return trainerWorkloadService.getMonthlyDuration(trainerUsername, year, month);
    }
}
