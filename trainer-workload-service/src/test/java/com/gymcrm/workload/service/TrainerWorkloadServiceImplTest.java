package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.ActionType;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerWorkloadServiceImplTest {
    private final TrainerWorkloadService service =
            new TrainerWorkloadServiceImpl(new TrainerWorkloadRepository());

    @Test
    void shouldAddAndDeleteMonthlyDuration() {
        service.apply(request(ActionType.ADD, 60));
        service.apply(request(ActionType.ADD, 30));
        service.apply(request(ActionType.DELETE, 45));

        assertEquals(45, service.getMonthlyDuration("Orkhan.Karimov", 2026, 5).trainingSummaryDuration());
    }

    @Test
    void shouldNotStoreNegativeDurationWhenDeleteExceedsCurrentSummary() {
        service.apply(request(ActionType.ADD, 30));
        service.apply(request(ActionType.DELETE, 90));

        assertEquals(0, service.getMonthlyDuration("Orkhan.Karimov", 2026, 5).trainingSummaryDuration());
    }

    private TrainerWorkloadRequest request(ActionType actionType, int duration) {
        return new TrainerWorkloadRequest("Orkhan.Karimov", "Orkhan", "Karimov", true,
                LocalDate.of(2026, 5, 1), duration, actionType);
    }
}
