package com.gymcrm.actuator;

import com.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeHealthIndicator implements HealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeHealthIndicator.class);

    private final TrainingService trainingService;

    public TrainingTypeHealthIndicator(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @Override
    public Health health() {
        int count = trainingService.getTrainingTypes().size();
        LOGGER.debug("Training type health checked, count={}", count);
        if (count == 0) {
            return Health.down()
                    .withDetail("trainingTypes", count)
                    .withDetail("reason", "No training types configured")
                    .build();
        }
        return Health.up().withDetail("trainingTypes", count).build();
    }
}
