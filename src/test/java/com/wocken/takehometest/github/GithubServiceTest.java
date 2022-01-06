package com.wocken.takehometest.github;

import com.wocken.takehometest.github.model.GithubRepo;
import com.wocken.takehometest.github.model.GithubUser;
import com.wocken.takehometest.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GithubServiceTest {

    private GithubClient githubClient;
    private GithubService classUnderTest;

    @BeforeEach
    void doBeforeEach() {
        githubClient = Mockito.mock(GithubClient.class);
        classUnderTest = new GithubService(githubClient);
    }

    @Test
    void fetchAndAggregateUserOrNull_happyPath() {
        String username = "some-username";
        GithubUser githubUser = new GithubUser(
                username,
                "some-name",
                "http://fake-avatar-url.com",
                "Minneapolis",
                null,
                "http://some-fake-html-url.com",
                new Date()
        );
        GithubRepo githubRepo = new GithubRepo(
                "some-repo-name",
                "http://some-fake-svn-url.com"
        );
        List<GithubRepo> githubRepos = new ArrayList<>();
        githubRepos.add(githubRepo);
        Mockito.when(githubClient.getGithubReposOrThrow(Mockito.eq(username))).thenReturn(githubRepos);
        Mockito.when(githubClient.getGithubUserOrThrow(Mockito.eq(username))).thenReturn(githubUser);

        User user = classUnderTest.fetchAndAggregateUserOrNull(username);

        assertEquals(username, user.getUsername());
        assertEquals(githubUser.getName(), user.getDisplayName());
        assertEquals(githubUser.getAvatarUrl(), user.getAvatarLink());
        assertEquals(githubUser.getLocation(), user.getGeoLocation());
        assertEquals(githubUser.getEmail(), user.getEmail());
        assertEquals(githubUser.getHtmlUrl(), user.getUrl());
        assertEquals(githubUser.getCreatedAt(), user.getCreatedAt());
        assertEquals(1, user.getRepos().size());
        assertEquals(githubRepo.getName(), user.getRepos().get(0).getName());
        assertEquals(githubRepo.getSvnUrl(), user.getRepos().get(0).getUrl());
    }

    @Test
    void fetchAndAggregateUserOrNull_githubReposNotFound_returnsNull() {
        String username = "some-username";
        Mockito.when(githubClient.getGithubReposOrThrow(Mockito.eq(username))).thenThrow(new GithubReposNotFoundException("some-error"));

        User user = classUnderTest.fetchAndAggregateUserOrNull(username);

        assertNull(user);
    }

    @Test
    void fetchAndAggregateUserOrNull_githubUserNotFound_returnsNull() {
        String username = "some-username";
        GithubRepo githubRepo = new GithubRepo(
                "some-repo-name",
                "http://some-fake-svn-url.com"
        );
        List<GithubRepo> githubRepos = new ArrayList<>();
        githubRepos.add(githubRepo);
        Mockito.when(githubClient.getGithubReposOrThrow(Mockito.eq(username))).thenReturn(githubRepos);
        Mockito.when(githubClient.getGithubUserOrThrow(Mockito.eq(username))).thenThrow(new GithubUserNotFoundException("some-error"));

        User user = classUnderTest.fetchAndAggregateUserOrNull(username);

        assertNull(user);
    }

    @Test
    void fetchAndAggregateUserOrNull_throwsGenericException() {
        String username = "some-username";
        Mockito.when(githubClient.getGithubReposOrThrow(Mockito.eq(username))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean exceptionThrown = false;
        try {
            classUnderTest.fetchAndAggregateUserOrNull(username);
        } catch (RuntimeException thrownException) {
            assertTrue(thrownException.getCause() instanceof HttpServerErrorException);
            assertEquals("Failed to fetch github information for user for some unknown reason. username=" + username, thrownException.getMessage());
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}
