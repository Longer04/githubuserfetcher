package com.example.githubuserfetcher.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO {

    public static final String NOT_CALCULABLE_FOLLOWERS_IS_ZERO = "Not calculable (followers is zero)";
    private String id;
    private String login;
    private String name;
    private String type;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("created_at")
    private String createdAt;
    private String calculations;

    @JsonCreator
    public UserDTO(
            @JsonProperty("id") String id,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("avatar_url") String avatarUrl,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("followers") int followers,
            @JsonProperty("public_repos") int publicRepos) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.calculations = calculate(followers, publicRepos);
    }

    private String calculate(int followers, int publicRepos) {
        if (followers == 0) {
            return NOT_CALCULABLE_FOLLOWERS_IS_ZERO;
        }

        double result = 6.0 / followers * (2 + publicRepos);
        return String.valueOf(result);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", calculations='" + calculations + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCalculations() {
        return calculations;
    }
}
