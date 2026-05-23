package com.gymcrm.rest.dto;

public record TrainerUpdateRequest(String username, String firstName, String lastName, Boolean active) {
}
