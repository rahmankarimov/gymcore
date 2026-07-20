package com.gymcrm.workload.service;

import com.gymcrm.workload.dto.ActionType;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.model.TrainerWorkload;
import com.gymcrm.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadServiceImpl service;

    private TrainerWorkload workload;

    @BeforeEach
    void setUp() {
        workload = new TrainerWorkload("Orkhan.Karimov", "Orkhan", "Karimov", true);
    }

    @Test
    void shouldCreateNewEntityWhenNotFound() {
        when(repository.findByTrainerUsername("Orkhan.Karimov")).thenReturn(Optional.empty());
        when(repository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> {
            workload = invocation.getArgument(0);
            return workload;
        });

        service.apply(request(ActionType.ADD, 60));

        when(repository.findByTrainerUsername("Orkhan.Karimov")).thenReturn(Optional.of(workload));

        assertEquals(60, service.getMonthlyDuration("Orkhan.Karimov", 2026, 5).trainingSummaryDuration());
    }

    @Test
    void shouldAddAndDeleteMonthlyDuration() {
        when(repository.findByTrainerUsername("Orkhan.Karimov")).thenReturn(Optional.of(workload));

        service.apply(request(ActionType.ADD, 60));
        service.apply(request(ActionType.ADD, 30));
        service.apply(request(ActionType.DELETE, 45));

        assertEquals(45, service.getMonthlyDuration("Orkhan.Karimov", 2026, 5).trainingSummaryDuration());
    }

    @Test
    void shouldNotStoreNegativeDurationWhenDeleteExceedsCurrentSummary() {
        when(repository.findByTrainerUsername("Orkhan.Karimov")).thenReturn(Optional.of(workload));

        service.apply(request(ActionType.ADD, 30));
        service.apply(request(ActionType.DELETE, 90));

        assertEquals(0, service.getMonthlyDuration("Orkhan.Karimov", 2026, 5).trainingSummaryDuration());
    }

    private TrainerWorkloadRequest request(ActionType actionType, int duration) {
        return new TrainerWorkloadRequest("Orkhan.Karimov", "Orkhan", "Karimov", true,
                LocalDate.of(2026, 5, 1), duration, actionType);
    }
}
