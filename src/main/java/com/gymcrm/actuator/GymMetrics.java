package com.gymcrm.actuator;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import com.gymcrm.dao.TrainingDao;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class GymMetrics {
    private static final Logger LOGGER = LoggerFactory.getLogger(GymMetrics.class);

    private final MeterRegistry meterRegistry;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingDao trainingDao;

    public GymMetrics(MeterRegistry meterRegistry, TraineeDao traineeDao, TrainerDao trainerDao,
                      TrainingDao trainingDao) {
        this.meterRegistry = meterRegistry;
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
        this.trainingDao = trainingDao;
    }

    @PostConstruct
    public void registerMetrics() {
        Gauge.builder("gymcrm.trainees.total", traineeDao, dao -> dao.findAll().size())
                .description("Current number of trainee profiles")
                .register(meterRegistry);
        Gauge.builder("gymcrm.trainers.total", trainerDao, dao -> dao.findAll().size())
                .description("Current number of trainer profiles")
                .register(meterRegistry);
        Gauge.builder("gymcrm.trainings.total", trainingDao, dao -> dao.findAll().size())
                .description("Current number of trainings")
                .register(meterRegistry);
        Gauge.builder("gymcrm.training.types.total", trainingDao, dao -> dao.findTrainingTypes().size())
                .description("Current number of training types")
                .register(meterRegistry);
        LOGGER.info("Registered custom Gym CRM gauges");
    }
}
