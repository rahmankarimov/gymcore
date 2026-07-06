package com.gymcrm.workload.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.workload.dto.TrainerWorkloadRequest;
import com.gymcrm.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TrainerWorkloadMessageListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerWorkloadMessageListener.class);
    private static final String TRANSACTION_ID_PROPERTY = "transactionId";

    private final TrainerWorkloadService trainerWorkloadService;
    private final ObjectMapper objectMapper;

    public TrainerWorkloadMessageListener(TrainerWorkloadService trainerWorkloadService, ObjectMapper objectMapper) {
        this.trainerWorkloadService = trainerWorkloadService;
        this.objectMapper = objectMapper;
    }

    @JmsListener(destination = "${workload.queue.trainer-workload}")
    public void acceptWorkload(@Payload String payload,
                               @Header(name = TRANSACTION_ID_PROPERTY, required = false) String transactionId)
            throws JsonProcessingException {
        if (transactionId != null && !transactionId.isBlank()) {
            MDC.put(TRANSACTION_ID_PROPERTY, transactionId);
        }
        try {
            TrainerWorkloadRequest request = objectMapper.readValue(payload, TrainerWorkloadRequest.class);
            LOGGER.info("Accept trainer workload message trainerUsername={} actionType={}",
                    request.trainerUsername(), request.actionType());
            trainerWorkloadService.apply(request);
        } finally {
            MDC.remove(TRANSACTION_ID_PROPERTY);
        }
    }
}
