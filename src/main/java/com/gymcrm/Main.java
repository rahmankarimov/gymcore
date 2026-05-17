package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymFacade facade = context.getBean(GymFacade.class);
            facade.getTraineeService().selectProfileById(1L)
                    .ifPresent(trainee -> LOGGER.info("Loaded sample trainee: {}", trainee));
            facade.getTrainerService().selectProfileById(1L)
                    .ifPresent(trainer -> LOGGER.info("Loaded sample trainer: {}", trainer));
            facade.getTrainingService().selectProfileById(1L)
                    .ifPresent(training -> LOGGER.info("Loaded sample training: {}", training));
        }
    }
}
