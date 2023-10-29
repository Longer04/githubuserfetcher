package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.transfer.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GithubApiServiceImplTest {

    public static final String TEST_USERNAME = "testUsername";
    @Autowired
    private GithubApiService githubApiService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void whenCalledTwice_thenSecondCallShouldUseCache() {
        final String username = TEST_USERNAME;

        final UserDTO firstCall = githubApiService.fetchUserFromGithub(username);
        final UserDTO secondCall = githubApiService.fetchUserFromGithub(username);

        final Cache userProfilesCache = cacheManager.getCache("userProfiles");
        assert userProfilesCache != null;
        final UserDTO cachedUser = userProfilesCache.get(username, UserDTO.class);

        assertThat(cachedUser).isEqualTo(firstCall);
        assertThat(secondCall).isSameAs(firstCall);
    }
}