package com.gymcrm.rest;

import com.gymcrm.domain.Trainer;
import com.gymcrm.exception.EntityNotFoundException;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.ActiveStateRequest;
import com.gymcrm.rest.dto.AuthCredentials;
import com.gymcrm.rest.dto.RegistrationResponse;
import com.gymcrm.rest.dto.TrainerProfileResponse;
import com.gymcrm.rest.dto.TrainerRegistrationRequest;
import com.gymcrm.rest.dto.TrainerTrainingResponse;
import com.gymcrm.rest.dto.TrainerUpdateRequest;
import com.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

@Api(tags = "Trainers")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {
    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @ApiOperation("Register trainer profile")
    @PostMapping
    public RegistrationResponse register(@RequestBody TrainerRegistrationRequest request) {
        RestSupport.requireText(request.firstName(), "First name");
        RestSupport.requireText(request.lastName(), "Last name");
        RestSupport.requireText(request.specialization(), "Specialization");
        Trainer trainer = trainerService.createProfile(new Trainer(null, request.firstName(), request.lastName(),
                null, null, true, request.specialization()));
        return new RegistrationResponse(trainer.getUsername(), trainer.getPassword());
    }

    @ApiOperation("Get trainer profile")
    @GetMapping("/profile")
    public TrainerProfileResponse getProfile(@RequestParam("username") String username,
                                             HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        Trainer trainer = trainerService.selectProfileByUsername(username, credentials.password())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        return RestSupport.trainerProfile(trainer);
    }

    @ApiOperation("Update trainer profile")
    @PutMapping("/profile")
    public TrainerProfileResponse updateProfile(@RequestBody TrainerUpdateRequest request,
                                                HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireOwner(request.username(), credentials);
        RestSupport.requireText(request.firstName(), "First name");
        RestSupport.requireText(request.lastName(), "Last name");
        if (request.active() == null) {
            throw new ValidationException("Is active is required");
        }
        Trainer existing = trainerService.selectProfileByUsername(request.username(), credentials.password())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + request.username()));
        Trainer updated = new Trainer(existing.getId(), request.firstName(), request.lastName(), existing.getUsername(),
                existing.getPassword(), request.active(), existing.getSpecialization());
        updated.setTrainees(existing.getTrainees());
        return RestSupport.trainerProfile(trainerService.updateProfile(updated));
    }

    @ApiOperation("Get trainer trainings list")
    @GetMapping("/trainings")
    public List<TrainerTrainingResponse> getTrainings(
            @RequestParam("username") String username,
            @RequestParam(value = "periodFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodFrom,
            @RequestParam(value = "periodTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodTo,
            @RequestParam(value = "traineeName", required = false) String traineeName,
            HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireOwner(username, credentials);
        return RestSupport.trainerTrainings(trainerService.getTrainings(
                username, credentials.password(), periodFrom, periodTo, traineeName));
    }

    @ApiOperation("Activate or deactivate trainer")
    @PatchMapping("/active")
    public ResponseEntity<Void> changeActive(@RequestBody ActiveStateRequest request, HttpServletRequest servletRequest) {
        AuthCredentials credentials = RestSupport.basicAuth(servletRequest);
        RestSupport.requireText(request.username(), "Username");
        RestSupport.requireOwner(request.username(), credentials);
        if (request.active() == null) {
            throw new ValidationException("Is active is required");
        }
        trainerService.changeActiveState(request.username(), credentials.password(), request.active());
        return ResponseEntity.ok().build();
    }
}
