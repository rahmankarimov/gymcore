package com.gymcrm.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RestLoggingFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestLoggingFilter.class);
    private final MeterRegistry meterRegistry;

    public RestLoggingFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            LOGGER.info("REST request transactionId={} method={} uri={} query={}",
                    transactionId, request.getMethod(), request.getRequestURI(), request.getQueryString());
            filterChain.doFilter(request, response);
            Counter.builder("gymcrm.rest.calls.total")
                    .tag("method", request.getMethod())
                    .tag("uri", request.getRequestURI())
                    .tag("status", String.valueOf(response.getStatus()))
                    .register(meterRegistry)
                    .increment();
            LOGGER.info("REST response transactionId={} status={} durationMs={}",
                    transactionId, response.getStatus(), sample.stop(Timer.builder("gymcrm.rest.calls.duration")
                            .tag("method", request.getMethod())
                            .tag("uri", request.getRequestURI())
                            .tag("status", String.valueOf(response.getStatus()))
                            .register(meterRegistry)) / 1_000_000);
        } finally {
            MDC.remove("transactionId");
        }
    }
}
