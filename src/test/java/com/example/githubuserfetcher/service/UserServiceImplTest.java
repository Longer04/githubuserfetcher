package com.example.githubuserfetcher.service;

import com.example.githubuserfetcher.data.ApiRequestCount;
import com.example.githubuserfetcher.data.ApiRequestCountRepository;
import com.example.githubuserfetcher.transfer.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    public static final String TEST_USER = "testUser";
    @Mock
    private ApiRequestCountRepository apiRequestCountRepository;

    @Mock
    private GithubApiService githubApiService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUser_ShouldRetrieveUserAndIncrementCount_WhenUserExists() {
        final String login = TEST_USER;
        final UserDTO mockUserDTO = createUserDTO(TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, 1, 2);
        final ApiRequestCount mockApiRequestCount = new ApiRequestCount(login, 0L);

        when(githubApiService.fetchUserFromGithub(login)).thenReturn(mockUserDTO);
        when(apiRequestCountRepository.findById(login)).thenReturn(Optional.of(mockApiRequestCount));

        final UserDTO result = userService.getUser(login);

        assertThat(result).isEqualTo(mockUserDTO);
        verify(apiRequestCountRepository, times(1)).save(any(ApiRequestCount.class));
    }

    @Test
    void getUser_ShouldCreateNewCount_WhenUserDoesNotExist() {
        final ArgumentCaptor<ApiRequestCount> apiRequestCountArgumentCaptor = ArgumentCaptor.forClass(ApiRequestCount.class);
        final String login = TEST_USER;
        final UserDTO mockUserDTO = createUserDTO(TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, 1, 2);

        when(githubApiService.fetchUserFromGithub(login)).thenReturn(mockUserDTO);
        when(apiRequestCountRepository.findById(login)).thenReturn(Optional.empty());

        userService.getUser(login);

        verify(apiRequestCountRepository).save(apiRequestCountArgumentCaptor.capture());
        final ApiRequestCount savedApiRequestCount = apiRequestCountArgumentCaptor.getValue();
        assertThat(savedApiRequestCount.getLogin()).isEqualTo(login);
        assertThat(savedApiRequestCount.getRequestCount()).isEqualTo(1L);
    }

    @Test
    void getUser_whenUserExists_shouldIncrementRequestCount() {
        final String login = TEST_USER;
        final UserDTO mockUserDto = new UserDTO(TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, 10, 20);
        final ApiRequestCount existingApiRequestCount = new ApiRequestCount(login, 5L);

        when(githubApiService.fetchUserFromGithub(login)).thenReturn(mockUserDto);
        when(apiRequestCountRepository.findById(login)).thenReturn(Optional.of(existingApiRequestCount));

        userService.getUser(login);

        final ArgumentCaptor<ApiRequestCount> apiRequestCountCaptor = ArgumentCaptor.forClass(ApiRequestCount.class);
        verify(apiRequestCountRepository).save(apiRequestCountCaptor.capture());
        final ApiRequestCount updatedRequestCount = apiRequestCountCaptor.getValue();
        assertEquals(6, updatedRequestCount.getRequestCount());
    }

    @Test
    void getUser_whenApiThrowsRuntimeException_shouldPropagateException() {
        final String login = "userWithError";
        when(githubApiService.fetchUserFromGithub(login)).thenThrow(new RuntimeException("API error"));

        assertThrows(RuntimeException.class, () -> userService.getUser(login));
    }

    private UserDTO createUserDTO(final String id, final String login, final String name, final String type,
                                  final String avatarUrl, final String createdAt, final int followers,
                                  final int publicRepos) {
        return new UserDTO(id, login, name, type, avatarUrl, createdAt, followers, publicRepos);
    }

}