package com.gymcrm.workload.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.workload.dto.ActionType;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.service.TrainerWorkloadService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadMessageListenerTest {
    private final TrainerWorkloadService trainerWorkloadService = mock(TrainerWorkloadService.class);
    private final TrainerWorkloadMessageListener listener =
            new TrainerWorkloadMessageListener(trainerWorkloadService, objectMapper());

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldApplyTrainerWorkloadMessage() throws Exception {
        String payload = "{\"trainerUsername\":\"Orkhan.Karimov\",\"trainerFirstName\":\"Orkhan\","
                + "\"trainerLastName\":\"Karimov\",\"active\":true,\"trainingDate\":\"2026-05-01\","
                + "\"trainingDuration\":60,\"actionType\":\"ADD\"}";

        listener.acceptWorkload(payload, "tx-123");

        ArgumentCaptor<TrainerWorkloadRequest> request = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerWorkloadService).apply(request.capture());
        assertEquals("Orkhan.Karimov", request.getValue().trainerUsername());
        assertEquals(LocalDate.of(2026, 5, 1), request.getValue().trainingDate());
        assertEquals(ActionType.ADD, request.getValue().actionType());
        assertNull(MDC.get("transactionId"));
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
