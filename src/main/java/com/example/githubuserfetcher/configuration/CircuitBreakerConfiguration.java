package com.example.githubuserfetcher.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Configuration
public class CircuitBreakerConfiguration {

    public static final String SERVICE_CALL_SUCCEEDED = "Service call succeeded: {}";
    public static final String SERVICE_CALL_FAILED = "Service call failed: {}";
    public static final String CIRCUIT_BREAKER_STATE_CHANGED = "CircuitBreaker state changed: {}";
    public static final String CALL_NOT_PERMITTED_BY_CIRCUIT_BREAKER = "Call not permitted by CircuitBreaker: {}";
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);

    @Bean
    public CircuitBreakerConfig customCircuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .waitDurationInOpenState(Duration.ofSeconds(10))
                .failureRateThreshold(50)
                .recordExceptions(IOException.class, TimeoutException.class, HttpClientErrorException.class)
                .build();
    }

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig circuitBreakerConfig) {
        return CircuitBreakerRegistry.of(circuitBreakerConfig);
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerConfig circuitBreakerConfig) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("githubApiServiceCircuitBreaker", circuitBreakerConfig);
        configureCircuitBreakerEventListeners(circuitBreaker);
        return circuitBreaker;
    }

    private void configureCircuitBreakerEventListeners(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> logger.info(SERVICE_CALL_SUCCEEDED, event))
                .onError(event -> logger.error(SERVICE_CALL_FAILED, event))
                .onStateTransition(event -> logger.info(CIRCUIT_BREAKER_STATE_CHANGED, event))
                .onCallNotPermitted(event -> logger.info(CALL_NOT_PERMITTED_BY_CIRCUIT_BREAKER, event));
    }
}
