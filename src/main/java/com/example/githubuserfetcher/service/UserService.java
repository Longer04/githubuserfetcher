package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.transfer.UserDTO;

public interface UserService {

    UserDTO getUser(String login);
}
