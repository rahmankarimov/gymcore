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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestLoggingFilter extends OncePerRequestFilter {
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestLoggingFilter.class);
    private final MeterRegistry meterRegistry;

    public RestLoggingFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = transactionId(request);
        MDC.put("transactionId", transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);
        Timer.Sample sample = Timer.start(meterRegistry);
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        try {
            filterChain.doFilter(wrappedRequest, response);
            Counter.builder("gymcrm.rest.calls.total")
                    .tag("method", request.getMethod())
                    .tag("uri", request.getRequestURI())
                    .tag("status", String.valueOf(response.getStatus()))
                    .register(meterRegistry)
                    .increment();
            LOGGER.info("REST transaction transactionId={} method={} uri={} query={} request={} status={} durationMs={}",
                    transactionId, request.getMethod(), request.getRequestURI(), request.getQueryString(),
                    requestBody(wrappedRequest), response.getStatus(), sample.stop(Timer.builder("gymcrm.rest.calls.duration")
                            .tag("method", request.getMethod())
                            .tag("uri", request.getRequestURI())
                            .tag("status", String.valueOf(response.getStatus()))
                            .register(meterRegistry)) / 1_000_000);
        } finally {
            MDC.remove("transactionId");
        }
    }

    private String transactionId(HttpServletRequest request) {
        String existing = request.getHeader(TRANSACTION_ID_HEADER);
        return existing == null || existing.isBlank() ? UUID.randomUUID().toString() : existing;
    }

    private String requestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        String body = new String(content, StandardCharsets.UTF_8).replaceAll("\\s+", " ").trim();
        return body.length() > 1000 ? body.substring(0, 1000) + "..." : body;
    }
}
