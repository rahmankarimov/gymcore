package com.gymcrm.workload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class JmsWorkloadEventPublisher implements WorkloadEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsWorkloadEventPublisher.class);
    private static final String TRANSACTION_ID_PROPERTY = "transactionId";

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final String queueName;

    public JmsWorkloadEventPublisher(JmsTemplate jmsTemplate,
                                     ObjectMapper objectMapper,
                                     @Value("${workload.queue.trainer-workload}") String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
        this.queueName = queueName;
    }

    @Override
    public void publish(TrainerWorkloadRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(request);
            String transactionId = MDC.get(TRANSACTION_ID_PROPERTY);
            jmsTemplate.convertAndSend(queueName, payload, message -> withTransactionId(message, transactionId));
            LOGGER.info("Published trainer workload message trainerUsername={} actionType={}",
                    request.trainerUsername(), request.actionType());
        } catch (JsonProcessingException | JmsException exception) {
            LOGGER.warn("Trainer workload message was not delivered trainerUsername={} actionType={} reason={}",
                    request == null ? null : request.trainerUsername(),
                    request == null ? null : request.actionType(),
                    exception.getMessage());
        }
    }

    private Message withTransactionId(Message message, String transactionId) throws JMSException {
        if (transactionId != null && !transactionId.isBlank()) {
            message.setStringProperty(TRANSACTION_ID_PROPERTY, transactionId);
        }
        return message;
    }
}
