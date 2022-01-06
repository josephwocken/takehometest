package com.wocken.takehometest.github;

import com.wocken.takehometest.github.model.GithubRepo;
import com.wocken.takehometest.github.model.GithubUser;
import com.wocken.takehometest.model.Repo;
import com.wocken.takehometest.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final GithubClient githubClient;
    private final Logger log = LoggerFactory.getLogger(GithubService.class);

    @Autowired
    public GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    public User fetchAndAggregateUserOrNull(String username) {
        if (null == username || username.isEmpty()) {
            log.error("Cannot provide null, or empty username. Returning null. username=" + username);
            return null;
        }
        User returnUser = null;
        try {
            List<GithubRepo> repos = githubClient.getGithubReposOrThrow(username);
            GithubUser user = githubClient.getGithubUserOrThrow(username);
            returnUser = mapToUser(repos, user);
        } catch (GithubUserNotFoundException userNotFoundException) {
            log.error("Could not find user in github. Returning null. username=" + username, userNotFoundException);
        } catch (GithubReposNotFoundException reposNotFoundException) {
            log.error("Could not find repos for user in github. Returning null. username=" + username, reposNotFoundException);
        } catch (RuntimeException runtimeException) {
            String message = "Failed to fetch github information for user for some unknown reason. username=" + username;
            log.error(message, runtimeException);
            throw new RuntimeException(message, runtimeException);
        }
        return returnUser;
    }

    private User mapToUser(List<GithubRepo> repos, GithubUser user) {
        return new User(
                user.getLogin(),
                user.getName(),
                user.getAvatarUrl(),
                user.getLocation(),
                user.getEmail(),
                user.getHtmlUrl(),
                user.getCreatedAt(),
                mapToRepos(repos)
        );
    }

    private List<Repo> mapToRepos(List<GithubRepo> githubRepos) {
        return githubRepos.stream()
                .map((GithubRepo githubRepo) -> new Repo(
                        githubRepo.getName(),
                        githubRepo.getSvnUrl()
                ))
                .collect(Collectors.toList());
    }
}
