package com.example.githubuserfetcher.exception;

public class GithubRateLimitException extends RuntimeException {
    public GithubRateLimitException(final String message) {
        super(message);
    }
}
