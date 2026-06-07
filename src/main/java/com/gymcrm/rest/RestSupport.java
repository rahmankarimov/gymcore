package com.gymcrm.rest;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.TraineeProfileResponse;
import com.gymcrm.rest.dto.TraineeSummaryResponse;
import com.gymcrm.rest.dto.TraineeTrainingResponse;
import com.gymcrm.rest.dto.TrainerProfileResponse;
import com.gymcrm.rest.dto.TrainerSummaryResponse;
import com.gymcrm.rest.dto.TrainerTrainingResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Comparator;
import java.util.List;

final class RestSupport {
    private RestSupport() {
    }

    static String authenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Authentication is required");
        }
        return authentication.getName();
    }

    static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    static void requireOwner(String requestedUsername, String authenticatedUsername) {
        if (!requestedUsername.equals(authenticatedUsername)) {
            throw new SecurityException("Authenticated user does not match requested username");
        }
    }

    static TraineeProfileResponse traineeProfile(Trainee trainee) {
        return new TraineeProfileResponse(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive(),
                trainee.getTrainers().stream()
                        .sorted(Comparator.comparing(Trainer::getUsername))
                        .map(RestSupport::trainerSummary)
                        .toList());
    }

    static TrainerProfileResponse trainerProfile(Trainer trainer) {
        return new TrainerProfileResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getSpecialization(),
                trainer.isActive(),
                trainer.getTrainees().stream()
                        .sorted(Comparator.comparing(Trainee::getUsername))
                        .map(RestSupport::traineeSummary)
                        .toList());
    }

    static TrainerSummaryResponse trainerSummary(Trainer trainer) {
        return new TrainerSummaryResponse(trainer.getUsername(), trainer.getFirstName(),
                trainer.getLastName(), trainer.getSpecialization());
    }

    static TraineeSummaryResponse traineeSummary(Trainee trainee) {
        return new TraineeSummaryResponse(trainee.getUsername(), trainee.getFirstName(), trainee.getLastName());
    }

    static List<TraineeTrainingResponse> traineeTrainings(List<Training> trainings) {
        return trainings.stream()
                .map(training -> new TraineeTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType(),
                        training.getTrainingDuration(),
                        training.getTrainer().getFirstName() + " " + training.getTrainer().getLastName()))
                .toList();
    }

    static List<TrainerTrainingResponse> trainerTrainings(List<Training> trainings) {
        return trainings.stream()
                .map(training -> new TrainerTrainingResponse(
                        training.getTrainingName(),
                        training.getTrainingDate(),
                        training.getTrainingType(),
                        training.getTrainingDuration(),
                        training.getTrainee().getFirstName() + " " + training.getTrainee().getLastName()))
                .toList();
    }
}
