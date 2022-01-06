package com.wocken.takehometest.github;

import com.wocken.takehometest.github.model.GithubRepo;
import com.wocken.takehometest.github.model.GithubUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GithubClient {

    private final RestTemplate restTemplate;
    private final String githubBaseUrl;

    @Autowired
    public GithubClient(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.githubBaseUrl = Objects.requireNonNull(environment.getProperty("githubBaseUrl"), "githubBaseUrl cannot be null");
    }

    public List<GithubRepo> getGithubReposOrThrow(String username) {
        URI uri = UriComponentsBuilder.fromUriString(githubBaseUrl)
                .pathSegment("users", username, "repos")
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .build();
        ResponseEntity<List<GithubRepo>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException clientException) {
            if (clientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GithubReposNotFoundException("Failed to find github repos for user. username=" + username, clientException);
            } else {
                throw clientException;
            }
        } catch (RuntimeException runtimeException) {
            throw new RuntimeException("Failed to fetch github repos for user. username=" + username, runtimeException);
        }
        List<GithubRepo> repos = new ArrayList<>();
        if (null != responseEntity.getBody()) {
            repos.addAll(responseEntity.getBody());
        }
        return repos;
    }

    public GithubUser getGithubUserOrThrow(String username) {
        URI uri = UriComponentsBuilder.fromUriString(githubBaseUrl)
                .pathSegment("users", username)
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .build();
        ResponseEntity<GithubUser> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, GithubUser.class);
        } catch (HttpClientErrorException clientException) {
            if (clientException.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new GithubUserNotFoundException("Failed to find github user. username=" + username);
            } else {
                throw clientException;
            }
        } catch (RuntimeException runtimeException) {
            throw new RuntimeException("Failed to fetch github user. username=" + username, runtimeException);
        }
        return responseEntity.getBody();
    }
}
