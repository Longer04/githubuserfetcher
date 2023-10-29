package com.example.githubuserfetcher.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenUserNotFoundException_thenResponseStatusShouldBeNotFound() {
        final UserNotFoundException exception = new UserNotFoundException("User not found");
        final ResponseEntity<String> response = globalExceptionHandler.handleUserNotFound(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("User not found");
    }

    @Test
    void whenGenericWebClientException_thenResponseStatusShouldBeInternalServerError() {
        final GenericWebClientException exception = new GenericWebClientException("Generic error");
        final ResponseEntity<String> response = globalExceptionHandler.handleGenericWebClientException(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Generic error");
    }
}