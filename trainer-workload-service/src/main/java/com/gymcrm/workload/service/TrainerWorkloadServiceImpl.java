package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.ActionType;
import com.gymcrm.workload.dto.MonthSummaryResponse;
import com.gymcrm.workload.dto.MonthlyDurationResponse;
import com.gymcrm.workload.dto.TrainerSummaryResponse;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.dto.YearSummaryResponse;
import com.gymcrm.workload.model.MonthSummary;
import com.gymcrm.workload.model.TrainerWorkload;
import com.gymcrm.workload.model.YearSummary;
import com.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);

    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadServiceImpl(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void apply(TrainerWorkloadRequest request) {
        validate(request);
        LOGGER.info("Starting transaction to apply workload action trainerUsername={} actionType={} duration={} date={}",
                request.trainerUsername(), request.actionType(), request.trainingDuration(), request.trainingDate());

        int durationDelta = request.actionType() == ActionType.ADD
                ? request.trainingDuration()
                : -request.trainingDuration();

        Optional<TrainerWorkload> optionalWorkload = repository.findByTrainerUsername(request.trainerUsername());
        TrainerWorkload workload;

        if (optionalWorkload.isEmpty()) {
            LOGGER.info("Trainer document does not exist, creating new record for username={}", request.trainerUsername());
            workload = new TrainerWorkload(request.trainerUsername(), request.trainerFirstName(),
                    request.trainerLastName(), request.active());
        } else {
            LOGGER.info("Trainer profile extracted successfully for username={}", request.trainerUsername());
            workload = optionalWorkload.get();
            workload.setTrainerFirstName(request.trainerFirstName());
            workload.setTrainerLastName(request.trainerLastName());
            workload.setActive(request.active());
        }

        int year = request.trainingDate().getYear();
        int month = request.trainingDate().getMonthValue();

        YearSummary yearSummary = workload.getYearsList().stream()
                .filter(y -> y.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearSummary newYear = new YearSummary(year);
                    workload.getYearsList().add(newYear);
                    return newYear;
                });

        MonthSummary monthSummary = yearSummary.getMonthsList().stream()
                .filter(m -> m.getMonth() == month)
                .findFirst()
                .orElseGet(() -> {
                    MonthSummary newMonth = new MonthSummary(month, 0);
                    yearSummary.getMonthsList().add(newMonth);
                    return newMonth;
                });

        int newDuration = Math.max(0, monthSummary.getTrainingsSummaryDuration() + durationDelta);
        monthSummary.setTrainingsSummaryDuration(newDuration);

        LOGGER.info("Updating duration for year={} month={} to {}", year, month, newDuration);
        repository.save(workload);

        LOGGER.info("Successfully applied workload action and saved document for trainerUsername={}", request.trainerUsername());
    }

    @Override
    public TrainerSummaryResponse getSummary(String trainerUsername) {
        TrainerWorkload workload = repository.findByTrainerUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer workload not found: " + trainerUsername));
        return toResponse(workload);
    }

    @Override
    public MonthlyDurationResponse getMonthlyDuration(String trainerUsername, int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        TrainerWorkload workload = repository.findByTrainerUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer workload not found: " + trainerUsername));
        
        int duration = workload.getYearsList().stream()
                .filter(y -> y.getYear() == year)
                .flatMap(y -> y.getMonthsList().stream())
                .filter(m -> m.getMonth() == month)
                .map(MonthSummary::getTrainingsSummaryDuration)
                .findFirst()
                .orElse(0);

        return new MonthlyDurationResponse(trainerUsername, year, month, duration);
    }

    private TrainerSummaryResponse toResponse(TrainerWorkload workload) {
        return new TrainerSummaryResponse(
                workload.getTrainerUsername(),
                workload.getTrainerFirstName(),
                workload.getTrainerLastName(),
                workload.isActive(),
                workload.getYearsList().stream()
                        .sorted(Comparator.comparingInt(YearSummary::getYear))
                        .map(yearSummary -> new YearSummaryResponse(
                                yearSummary.getYear(),
                                yearSummary.getMonthsList().stream()
                                        .sorted(Comparator.comparingInt(MonthSummary::getMonth))
                                        .map(monthSummary -> new MonthSummaryResponse(monthSummary.getMonth(), monthSummary.getTrainingsSummaryDuration()))
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
