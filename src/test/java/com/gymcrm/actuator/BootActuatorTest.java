package com.gymcrm.actuator;

import com.gymcrm.Main;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("local")
@SpringBootTest(classes = Main.class)
class BootActuatorTest {

    @Autowired
    private Environment environment;

    @Autowired
    private TrainingTypeHealthIndicator trainingTypeHealthIndicator;

    @Autowired
    private ProfileDataHealthIndicator profileDataHealthIndicator;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void shouldLoadLocalProfileDatabaseProperties() {
        assertEquals("jdbc:h2:mem:gymcore-local;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                environment.getProperty("spring.datasource.url"));
        assertEquals("local", environment.getActiveProfiles()[0]);
    }

    @Test
    void shouldExposeCustomHealthIndicators() {
        Health trainingTypes = trainingTypeHealthIndicator.health();
        Health profileData = profileDataHealthIndicator.health();

        assertEquals(Status.UP, trainingTypes.getStatus());
        assertEquals(Status.UP, profileData.getStatus());
        assertTrue((Integer) trainingTypes.getDetails().get("trainingTypes") > 0);
        assertNotNull(profileData.getDetails().get("trainees"));
        assertNotNull(profileData.getDetails().get("trainers"));
    }

    @Test
    void shouldRegisterCustomPrometheusMetrics() {
        assertNotNull(meterRegistry.find("gymcrm.trainees.total").gauge());
        assertNotNull(meterRegistry.find("gymcrm.trainers.total").gauge());
        assertNotNull(meterRegistry.find("gymcrm.trainings.total").gauge());
        assertNotNull(meterRegistry.find("gymcrm.training.types.total").gauge());
    }
}
