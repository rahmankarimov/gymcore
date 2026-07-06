package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.ActionType;
import com.gymcrm.workload.dto.MonthSummaryResponse;
import com.gymcrm.workload.dto.MonthlyDurationResponse;
import com.gymcrm.workload.dto.TrainerSummaryResponse;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.dto.YearSummaryResponse;
import com.gymcrm.workload.model.TrainerWorkload;
import com.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);

    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadServiceImpl(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void apply(TrainerWorkloadRequest request) {
        validate(request);
        int durationDelta = request.actionType() == ActionType.ADD
                ? request.trainingDuration()
                : -request.trainingDuration();
        TrainerWorkload workload = repository.getOrCreate(request.trainerUsername(), request.trainerFirstName(),
                request.trainerLastName(), request.active());
        workload.apply(request.trainerFirstName(), request.trainerLastName(), request.active(),
                request.trainingDate().getYear(), request.trainingDate().getMonthValue(), durationDelta);
        LOGGER.info("Applied workload action trainerUsername={} actionType={} duration={} date={}",
                request.trainerUsername(), request.actionType(), request.trainingDuration(), request.trainingDate());
    }

    @Override
    public TrainerSummaryResponse getSummary(String trainerUsername) {
        TrainerWorkload workload = repository.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer workload not found: " + trainerUsername));
        return toResponse(workload);
    }

    @Override
    public MonthlyDurationResponse getMonthlyDuration(String trainerUsername, int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        TrainerWorkload workload = repository.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer workload not found: " + trainerUsername));
        int duration = workload.getDurationsByYearMonth().getOrDefault(year, java.util.Map.of())
                .getOrDefault(month, 0);
        return new MonthlyDurationResponse(trainerUsername, year, month, duration);
    }

    private TrainerSummaryResponse toResponse(TrainerWorkload workload) {
        return new TrainerSummaryResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.isActive(),
                workload.getDurationsByYearMonth().entrySet().stream()
                        .sorted(java.util.Map.Entry.comparingByKey())
                        .map(yearEntry -> new YearSummaryResponse(
                                yearEntry.getKey(),
                                yearEntry.getValue().entrySet().stream()
                                        .sorted(Comparator.comparingInt(java.util.Map.Entry::getKey))
                                        .map(monthEntry -> new MonthSummaryResponse(monthEntry.getKey(), monthEntry.getValue()))
                                        .toList()))
                        .toList());
    }

    private void validate(TrainerWorkloadRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        }
        requireText(request.trainerUsername(), "Trainer username");
        requireText(request.trainerFirstName(), "Trainer first name");
        requireText(request.trainerLastName(), "Trainer last name");
        if (request.active() == null) {
            throw new IllegalArgumentException("Trainer status is required");
        }
        if (request.trainingDate() == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (request.trainingDuration() == null || request.trainingDuration() <= 0) {
            throw new IllegalArgumentException("Training duration must be positive");
        }
        if (request.actionType() == null) {
            throw new IllegalArgumentException("Action type is required");
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
