package com.wocken.takehometest.github;

public class GithubReposNotFoundException extends RuntimeException {

    public GithubReposNotFoundException(String message) {
        super(message);
    }

    public GithubReposNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
