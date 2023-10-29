package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.data.ApiRequestCount;
import com.example.githubuserfetcher.data.ApiRequestCountRepository;
import com.example.githubuserfetcher.transfer.UserDTO;
import com.example.githubuserfetcher.util.UsernameMasker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String USER_RECEIVED_CALLS = "User: {} received {} calls.";
    public static final int COUNT_INCREMENT = 1;

    @Autowired
    private ApiRequestCountRepository apiRequestCountRepository;

    @Autowired
    private GithubApiService githubApiService;

    @Transactional
    public UserDTO getUser(final String login) {
        final UserDTO userDTO = githubApiService.fetchUserFromGithub(login);
        incrementApiRequestCount(login);
        return userDTO;
    }

    private void incrementApiRequestCount(final String login) {
        final ApiRequestCount apiRequestCount = apiRequestCountRepository.findById(login)
                .orElse(new ApiRequestCount(login, 0L));
        final long requestCount = apiRequestCount.getRequestCount() + COUNT_INCREMENT;
        apiRequestCount.setRequestCount(requestCount);
        logger.info(USER_RECEIVED_CALLS, UsernameMasker.maskUsername(login), requestCount);
        apiRequestCountRepository.save(apiRequestCount);
    }
}
