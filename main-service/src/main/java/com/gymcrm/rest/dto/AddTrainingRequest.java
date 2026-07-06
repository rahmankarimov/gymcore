package com.gymcrm.rest.dto;

import java.time.LocalDate;

public record AddTrainingRequest(String traineeUsername, String trainerUsername, String trainingName,
                                 LocalDate trainingDate, Integer trainingDuration) {
}
