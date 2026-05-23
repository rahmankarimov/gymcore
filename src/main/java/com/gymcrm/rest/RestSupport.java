package com.gymcrm.rest;

import com.gymcrm.domain.Trainee;
import com.gymcrm.domain.Trainer;
import com.gymcrm.domain.Training;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.AuthCredentials;
import com.gymcrm.rest.dto.TraineeProfileResponse;
import com.gymcrm.rest.dto.TraineeSummaryResponse;
import com.gymcrm.rest.dto.TraineeTrainingResponse;
import com.gymcrm.rest.dto.TrainerProfileResponse;
import com.gymcrm.rest.dto.TrainerSummaryResponse;
import com.gymcrm.rest.dto.TrainerTrainingResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

final class RestSupport {
    private RestSupport() {
    }

    static AuthCredentials basicAuth(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Basic ")) {
            throw new SecurityException("Basic authentication is required");
        }
        String decoded = new String(Base64.getDecoder().decode(header.substring(6)), StandardCharsets.UTF_8);
        int separator = decoded.indexOf(':');
        if (separator < 1) {
            throw new SecurityException("Invalid Basic authentication header");
        }
        return new AuthCredentials(decoded.substring(0, separator), decoded.substring(separator + 1));
    }

    static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    static void requireOwner(String requestedUsername, AuthCredentials credentials) {
        if (!requestedUsername.equals(credentials.username())) {
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
