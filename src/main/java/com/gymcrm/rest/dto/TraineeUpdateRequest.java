package com.gymcrm.rest.dto;

import java.time.LocalDate;

public record TraineeUpdateRequest(String username, String firstName, String lastName, LocalDate dateOfBirth,
                                   String address, Boolean active) {
}
