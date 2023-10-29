package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.transfer.UserDTO;

public interface GithubApiService {

    UserDTO fetchUserFromGithub(String username);
}
