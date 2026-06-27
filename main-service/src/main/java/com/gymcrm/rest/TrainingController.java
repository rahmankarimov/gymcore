package com.gymcrm.rest;

import com.gymcrm.domain.Training;
import com.gymcrm.exception.ValidationException;
import com.gymcrm.rest.dto.AddTrainingRequest;
import com.gymcrm.rest.dto.TrainingTypeResponse;
import com.gymcrm.rest.dto.TrainingTypesResponse;
import com.gymcrm.service.TrainingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Trainings")
@RestController
@RequestMapping("/api/trainings")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @ApiOperation("Add training")
    @PostMapping
    public ResponseEntity<Void> addTraining(@RequestBody AddTrainingRequest request) {
        RestSupport.requireText(request.traineeUsername(), "Trainee username");
        RestSupport.requireText(request.trainerUsername(), "Trainer username");
        RestSupport.requireText(request.trainingName(), "Training name");
        if (request.trainingDate() == null) {
            throw new ValidationException("Training date is required");
        }
        if (request.trainingDuration() == null) {
            throw new ValidationException("Training duration is required");
        }
        Training training = new Training(null, null, null, request.trainingName(), null,
                request.trainingDate(), request.trainingDuration());
        trainingService.createProfileForAuthenticatedUser(RestSupport.authenticatedUsername(),
                request.traineeUsername(), request.trainerUsername(), training);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Cancel training")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelTraining(@PathVariable("id") Long id) {
        trainingService.cancelTrainingForAuthenticatedUser(RestSupport.authenticatedUsername(), id);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get training types")
    @GetMapping("/types")
    public TrainingTypesResponse getTrainingTypes() {
        RestSupport.authenticatedUsername();
        return new TrainingTypesResponse(trainingService.getTrainingTypes().stream()
                .map(type -> new TrainingTypeResponse(type.getId(), type.getTrainingTypeName()))
                .toList());
    }
}
