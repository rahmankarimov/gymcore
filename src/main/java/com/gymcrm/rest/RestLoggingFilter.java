package com.gymcrm.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = UUID.randomUUID().toString();
        MDC.put("transactionId", transactionId);
        long start = System.currentTimeMillis();
        try {
            LOGGER.info("REST request transactionId={} method={} uri={} query={}",
                    transactionId, request.getMethod(), request.getRequestURI(), request.getQueryString());
            filterChain.doFilter(request, response);
            LOGGER.info("REST response transactionId={} status={} durationMs={}",
                    transactionId, response.getStatus(), System.currentTimeMillis() - start);
        } finally {
            MDC.remove("transactionId");
        }
    }
}
