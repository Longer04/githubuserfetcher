package com.example.githubuserfetcher.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "api_request_count")
public class ApiRequestCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @Column(name = "request_count", nullable = false)
    private Long requestCount;

    public ApiRequestCount() {
    }

    public ApiRequestCount(String login, Long requestCount) {
        this.login = login;
        this.requestCount = requestCount;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(final Long requestCount) {
        this.requestCount = requestCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiRequestCount that = (ApiRequestCount) o;
        return login != null && login.equals(that.login);
    }
}
