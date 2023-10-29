package com.example.githubuserfetcher.web;


// Import JUnit5 and Spring testing annotations

import com.example.githubuserfetcher.service.UserService;
import com.example.githubuserfetcher.transfer.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserWebService.class)
public class UserWebServiceTest {

    public static final String TEST_USER = "testUser";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldReturnUserInfoWhenUsernameIsValid() throws Exception {
        // Given
        String validUsername = TEST_USER;
        final UserDTO mockUserDto = new UserDTO(TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, TEST_USER, 10, 20);
        Mockito.when(userService.getUser(validUsername)).thenReturn(mockUserDto);

        // When
        ResultActions result = mockMvc.perform(get("/users/{username}", validUsername)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(validUsername)))
                .andExpect(jsonPath("$.type", is(validUsername)))
                .andExpect(jsonPath("$.login", is(validUsername)));
    }

    @Test
    public void shouldReturnBadRequestWhenUsernameIsBlank() throws Exception {
        // Given
        String invalidUsername = " ";

        // When
        ResultActions result = mockMvc.perform(get("/users/{username}", invalidUsername)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenUsernameIsInvalid() throws Exception {
        // Given
        String invalidUsername = "john@doe";

        // When
        ResultActions result = mockMvc.perform(get("/users/{username}", invalidUsername)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isBadRequest());
    }
}