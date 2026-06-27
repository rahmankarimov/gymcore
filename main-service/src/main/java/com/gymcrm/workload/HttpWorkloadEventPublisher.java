package com.gymcrm.workload;

import com.gymcrm.security.JwtService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpWorkloadEventPublisher implements WorkloadEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpWorkloadEventPublisher.class);
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    private final RestTemplate restTemplate;
    private final JwtService jwtService;
    private final String workloadServiceUrl;

    public HttpWorkloadEventPublisher(RestTemplate restTemplate,
                                      JwtService jwtService,
                                      @Value("${workload.service.url:http://trainer-workload-service}") String workloadServiceUrl) {
        this.restTemplate = restTemplate;
        this.jwtService = jwtService;
        this.workloadServiceUrl = workloadServiceUrl;
    }

    @Override
    @CircuitBreaker(name = "workloadService", fallbackMethod = "workloadFallback")
    public void publish(TrainerWorkloadRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtService.generateToken("gymcore-service"));
        String transactionId = MDC.get("transactionId");
        if (transactionId != null && !transactionId.isBlank()) {
            headers.set(TRANSACTION_ID_HEADER, transactionId);
        }
        restTemplate.postForEntity(workloadServiceUrl + "/api/trainer-workloads",
                new HttpEntity<>(request, headers), Void.class);
        LOGGER.info("Published trainer workload event trainerUsername={} actionType={}",
                request.trainerUsername(), request.actionType());
    }

    public void workloadFallback(TrainerWorkloadRequest request, Throwable exception) {
        LOGGER.warn("Trainer workload service unavailable; event was not delivered trainerUsername={} actionType={} reason={}",
                request == null ? null : request.trainerUsername(),
                request == null ? null : request.actionType(),
                exception.getMessage());
    }
}
