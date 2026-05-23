package com.gymcrm.rest.dto;

public record LoginChangeRequest(String username, String oldPassword, String newPassword) {
}
