package com.gymcrm.rest;

import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.LoginChangeRequest;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Authentication")
@RestController
@RequestMapping("/api")
public class AuthController {
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public AuthController(TraineeService traineeService, TrainerService trainerService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @ApiOperation("Login with username and password")
    @GetMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password) {
        RestSupport.requireText(username, "Username");
        RestSupport.requireText(password, "Password");
        if (traineeService.authenticate(username, password) || trainerService.authenticate(username, password)) {
            return ResponseEntity.ok().build();
        }
        throw new SecurityException("Invalid credentials");
    }

    @ApiOperation("Change user login password")
    @PutMapping("/login")
    public ResponseEntity<Void> changeLogin(@RequestBody LoginChangeRequest request) {
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireText(request.oldPassword(), "Old password");
        RestSupport.requireText(request.newPassword(), "New password");
        if (traineeService.authenticate(request.username(), request.oldPassword())) {
            traineeService.changePassword(request.username(), request.oldPassword(), request.newPassword());
            return ResponseEntity.ok().build();
        }
        if (trainerService.authenticate(request.username(), request.oldPassword())) {
            trainerService.changePassword(request.username(), request.oldPassword(), request.newPassword());
            return ResponseEntity.ok().build();
        }
        throw new SecurityException("Invalid credentials");
    }
}
