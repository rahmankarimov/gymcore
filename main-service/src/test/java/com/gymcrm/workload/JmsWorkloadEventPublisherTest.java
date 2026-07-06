package com.gymcrm.workload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JmsWorkloadEventPublisherTest {
    private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    private final JmsWorkloadEventPublisher publisher = new JmsWorkloadEventPublisher(
            jmsTemplate, objectMapper(), "trainer.workload.queue");

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldPublishTrainerWorkloadAsJsonMessage() {
        TrainerWorkloadRequest request = request();

        publisher.publish(request);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload.queue"), payload.capture(),
                org.mockito.ArgumentMatchers.any(MessagePostProcessor.class));
        assertEquals("{\"trainerUsername\":\"Orkhan.Karimov\",\"trainerFirstName\":\"Orkhan\","
                + "\"trainerLastName\":\"Karimov\",\"active\":true,\"trainingDate\":\"2026-05-01\","
                + "\"trainingDuration\":60,\"actionType\":\"ADD\"}", payload.getValue());
    }

    @Test
    void shouldPropagateTransactionIdAsMessageProperty() throws Exception {
        MDC.put("transactionId", "tx-123");

        publisher.publish(request());

        ArgumentCaptor<MessagePostProcessor> postProcessor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(jmsTemplate).convertAndSend(eq("trainer.workload.queue"), org.mockito.ArgumentMatchers.any(),
                postProcessor.capture());
        Message message = mock(Message.class);
        postProcessor.getValue().postProcessMessage(message);
        verify(message).setStringProperty("transactionId", "tx-123");
    }

    private TrainerWorkloadRequest request() {
        return new TrainerWorkloadRequest("Orkhan.Karimov", "Orkhan", "Karimov", true,
                LocalDate.of(2026, 5, 1), 60, WorkloadActionType.ADD);
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
