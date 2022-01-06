package com.wocken.takehometest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class User {
    @JsonProperty("username")
    private String username;
    @JsonProperty("display_name")
    private String displayName;
    @JsonProperty("avatar")
    private String avatarLink;
    @JsonProperty("geo_location")
    private String geoLocation;
    @JsonProperty("email")
    private String email;
    @JsonProperty("url")
    private String url;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdAt;
    @JsonProperty("repos")
    private List<Repo> repos;

    public User(String username, String displayName, String avatarLink, String geoLocation, String email, String url, Date createdAt, List<Repo> repos) {
        this.username = username;
        this.displayName = displayName;
        this.avatarLink = avatarLink;
        this.geoLocation = geoLocation;
        this.email = email;
        this.url = url;
        this.createdAt = createdAt;
        this.repos = repos;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public String getEmail() {
        return email;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public List<Repo> getRepos() {
        return repos;
    }
}
