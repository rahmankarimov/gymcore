package com.gymcrm.rest;

import com.gymcrm.domain.Trainee;
import com.gymcrm.exception.EntityNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.ActiveStateRequest;
import com.gymcrm.rest.dto.AuthCredentials;
import com.gymcrm.rest.dto.RegistrationResponse;
import com.gymcrm.rest.dto.TraineeProfileResponse;
import com.gymcrm.rest.dto.TraineeRegistrationRequest;
import com.gymcrm.rest.dto.TraineeTrainingResponse;
import com.gymcrm.rest.dto.TraineeUpdateRequest;
import com.gymcrm.rest.dto.TrainerListResponse;
import com.gymcrm.rest.dto.TrainerListUpdateRequest;
import com.gymcrm.rest.dto.TrainerSummaryResponse;
import com.gymcrm.service.TraineeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Api(tags = "Trainees")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {
    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @ApiOperation("Register trainee profile")
    @PostMapping
    public RegistrationResponse register(@RequestBody TraineeRegistrationRequest request) {
        RestSupport.requireText(request.firstName(), "First name");
        RestSupport.requireText(request.lastName(), "Last name");
        Trainee trainee = traineeService.createProfile(new Trainee(null, request.firstName(), request.lastName(),
                null, null, true, request.dateOfBirth(), request.address()));
        return new RegistrationResponse(trainee.getUsername(), trainee.getPassword());
    }

    @ApiOperation("Get trainee profile")
    @GetMapping("/profile")
    public TraineeProfileResponse getProfile(@RequestParam("username") String username,
                                             HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        Trainee trainee = traineeService.selectProfileByUsername(username, credentials.password())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        return RestSupport.traineeProfile(trainee);
    }

    @ApiOperation("Update trainee profile")
    @PutMapping("/profile")
    public TraineeProfileResponse updateProfile(@RequestBody TraineeUpdateRequest request,
                                                HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireOwner(request.username(), credentials);
        RestSupport.requireText(request.firstName(), "First name");
        RestSupport.requireText(request.lastName(), "Last name");
        if (request.active() == null) {
            throw new ValidationException("Is active is required");
        }
        Trainee existing = traineeService.selectProfileByUsername(request.username(), credentials.password())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + request.username()));
        Trainee updated = new Trainee(existing.getId(), request.firstName(), request.lastName(), existing.getUsername(),
                existing.getPassword(), request.active(), request.dateOfBirth(), request.address());
        updated.setTrainers(existing.getTrainers());
        return RestSupport.traineeProfile(traineeService.updateProfile(updated));
    }

    @ApiOperation("Delete trainee profile")
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(@RequestParam("username") String username,
                                              HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        traineeService.deleteProfileByUsername(username, credentials.password());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get active trainers not assigned to trainee")
    @GetMapping("/unassigned-trainers")
    public List<TrainerSummaryResponse> getUnassignedTrainers(@RequestParam("username") String username,
                                                              HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        return traineeService.getUnassignedTrainers(username, credentials.password()).stream()
                .filter(trainer -> trainer.isActive())
                .map(RestSupport::trainerSummary)
                .toList();
    }

    @ApiOperation("Update trainee trainers list")
    @PutMapping("/trainers")
    public TrainerListResponse updateTrainers(@RequestBody TrainerListUpdateRequest request,
                                              HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireText(request.traineeUsername(), "Trainee username");
        RestSupport.requireOwner(request.traineeUsername(), credentials);
        if (request.trainerUsernames() == null || request.trainerUsernames().isEmpty()) {
            throw new ValidationException("Trainers list is required");
        }
        Trainee trainee = traineeService.updateTrainers(request.traineeUsername(), credentials.password(),
                Set.copyOf(request.trainerUsernames()));
        return new TrainerListResponse(trainee.getTrainers().stream().map(RestSupport::trainerSummary).toList());
    }

    @ApiOperation("Get trainee trainings list")
    @GetMapping("/trainings")
    public List<TraineeTrainingResponse> getTrainings(
            @RequestParam("username") String username,
            @RequestParam(value = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(value = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(value = "trainerName", required = false) String trainerName,
            @RequestParam(value = "trainingType", required = false) String trainingType,
            HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        return RestSupport.traineeTrainings(traineeService.getTrainings(
                username, credentials.password(), periodFrom, periodTo, trainerName, trainingType));
    }

    @ApiOperation("Activate or deactivate trainee")
    @PatchMapping("/active")
    public ResponseEntity<Void> changeActive(@RequestBody ActiveStateRequest request, HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireOwner(request.username(), credentials);
        if (request.active() == null) {
            throw new ValidationException("Is active is required");
        }
        traineeService.changeActiveState(request.username(), credentials.password(), request.active());
        return ResponseEntity.ok().build();
    }
}
