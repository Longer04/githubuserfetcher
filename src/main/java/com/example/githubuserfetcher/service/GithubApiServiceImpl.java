package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.exception.GenericWebClientException;
import com.example.githubuserfetcher.exception.GithubRateLimitException;
import com.example.githubuserfetcher.exception.UserNotFoundException;
import com.example.githubuserfetcher.transfer.UserDTO;
import com.example.githubuserfetcher.util.UsernameMasker;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
public class GithubApiServiceImpl implements GithubApiService {
    public static final String X_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
    public static final String GITHUB_API_RATE_LIMIT_EXCEEDED_IT_TAKES_1_HOUR_TO_RESET = "Github API rate limit exceeded.. It takes 1 hour to reset.";
    public static final String STARTING_API_CALL_TO_FETCH_USER = "Starting API call to fetch user {}";
    public static final String SUCCESSFULLY_FETCHED_USER_TIME_TAKEN_MS = "Successfully fetched user {}. Time taken: {} ms";
    public static final String FAILED_TO_FETCH_USER_TIME_TAKEN_MS_ERROR = "Failed to fetch user {}. Time taken: {} ms. Error: {}";
    public static final String GITHUB_USER_NOT_FOUND = "Github user not found";
    public static final String GENERAL_ERROR_IN_GITHUB_OCCURRED = "General error in github occurred.";
    public static final String AN_UNEXPECTED_ERROR_OCCURRED_WHILE_FETCHING_USER_DATA = "An unexpected error occurred while fetching user data";
    private static final Logger logger = LoggerFactory.getLogger(GithubApiServiceImpl.class);
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;


    @Value("${github.api.url.users.path:/users/{username}}")
    private String githubApiUrl;

    @Autowired
    public GithubApiServiceImpl(@Autowired WebClient webClient, @Autowired CircuitBreaker circuitBreaker) {
        this.webClient = webClient;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    @Cacheable(value = "userProfiles", key = "#username", unless = "#result == null")
    public UserDTO fetchUserFromGithub(final String username) {
        final String maskedUsername = UsernameMasker.maskUsername(username);
        logger.info(STARTING_API_CALL_TO_FETCH_USER, maskedUsername);
        final long startTime = System.currentTimeMillis();
        try {
            return circuitBreaker.executeSupplier(() ->
                    webClient.get()
                            .uri(githubApiUrl, username)
                            .retrieve()
                            .onStatus(status -> status.value() == 404,
                                    response -> Mono.just(new UserNotFoundException(GITHUB_USER_NOT_FOUND)))
                            .onStatus(HttpStatusCode::is5xxServerError,
                                    response -> Mono.just(new GenericWebClientException(GENERAL_ERROR_IN_GITHUB_OCCURRED)))
                            .bodyToMono(UserDTO.class)
                            .doOnSuccess(user -> {
                                long duration = System.currentTimeMillis() - startTime;
                                logger.info(SUCCESSFULLY_FETCHED_USER_TIME_TAKEN_MS, maskedUsername, duration);
                            })
                            .doOnError(error -> {
                                long duration = System.currentTimeMillis() - startTime;
                                logger.error(FAILED_TO_FETCH_USER_TIME_TAKEN_MS_ERROR, maskedUsername, duration, error.getMessage());
                            })
                            .block()  // This is a blocking call
            );
        } catch (final WebClientResponseException | CallNotPermittedException | UserNotFoundException |
                       GenericWebClientException e) {
            if (e instanceof WebClientResponseException) {
                if (((WebClientResponseException) e).getHeaders().containsKey(X_RATE_LIMIT_REMAINING) &&
                        Integer.parseInt(Objects.requireNonNull(((WebClientResponseException) e).getHeaders().get(X_RATE_LIMIT_REMAINING)).get(0)) == (0)) {
                    throw new GithubRateLimitException(GITHUB_API_RATE_LIMIT_EXCEEDED_IT_TAKES_1_HOUR_TO_RESET);

                }
            }
            throw e;
        } catch (final Exception e) {
            throw new RuntimeException(AN_UNEXPECTED_ERROR_OCCURRED_WHILE_FETCHING_USER_DATA, e);
        }
    }
}
