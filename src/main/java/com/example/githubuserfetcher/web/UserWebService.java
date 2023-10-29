package com.example.githubuserfetcher.web;

import com.example.githubuserfetcher.service.UserService;
import com.example.githubuserfetcher.transfer.UserDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/users")
public class UserWebService {

    private final UserService userService;

    public UserWebService(@Autowired UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserInfo(@PathVariable
                                               @NotBlank(message = "Username must not be blank")
                                               @Pattern(regexp = "^[a-zA-Z0-9]+(?:-[a-zA-Z0-9]+)*$", message = "Username must be alphanumeric with optional dashes")
                                               String username) {
        return ResponseEntity.ok(userService.getUser(username));
    }
}
