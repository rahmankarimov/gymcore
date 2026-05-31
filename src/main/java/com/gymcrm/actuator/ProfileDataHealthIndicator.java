package com.gymcrm.actuator;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.dao.TrainerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ProfileDataHealthIndicator implements HealthIndicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileDataHealthIndicator.class);

    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public ProfileDataHealthIndicator(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @Override
    public Health health() {
        int trainees = traineeDao.findAll().size();
        int trainers = trainerDao.findAll().size();
        LOGGER.debug("Profile data health checked, trainees={}, trainers={}", trainees, trainers);
        return Health.up()
                .withDetail("trainees", trainees)
                .withDetail("trainers", trainers)
                .build();
    }
}
