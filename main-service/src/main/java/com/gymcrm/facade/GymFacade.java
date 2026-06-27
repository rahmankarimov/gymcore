package com.gymcrm.facade;

import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public TraineeService getTraineeService() {
        return traineeService;
    }

    public TrainerService getTrainerService() {
        return trainerService;
    }

    public TrainingService getTrainingService() {
        return trainingService;
    }
}
