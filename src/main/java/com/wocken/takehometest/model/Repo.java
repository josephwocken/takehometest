package com.wocken.takehometest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Repo {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("url")
    private final String url;

    public Repo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
