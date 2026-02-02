package com.yankov.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.yankov.backend.enums.Role;
import com.yankov.backend.model.User;
import com.yankov.backend.security.CustomUserDetailsService;
import com.yankov.backend.security.JwtService;
import com.yankov.backend.service.UserService;
import com.yankov.backend.model.dto.request.UserCreateRequestDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Disable security filters until JWT is implemented
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock service layer to isolate controller behavior
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // Test constants
    private static final Long USER_ID = 1L;
    private static final String EMAIL = "test@example.com";
    private static final String FULL_NAME = "Test User";
    private static final String USER_ROLE = "USER";
    private static final String PASSWORD = "password123";

    // GET /api/users/{id}
    @Test
    void getUserById_shouldReturnUser_whenExists() throws Exception {

        // Arrange
        User user = User.builder()
                .id(USER_ID)
                .fullName(FULL_NAME)
                .email(EMAIL)
                .role(Role.USER)
                .created_At(LocalDateTime.now())
                .build();

        when(userService.getUserById(USER_ID)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.fullName").value(FULL_NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value(USER_ROLE));
    }

    // GET /api/users/by-email
    @Test
    void getUserByEmail_shouldReturnUser_whenExists() throws Exception {

        // Arrange
        User user = User.builder()
                .id(USER_ID)
                .fullName(FULL_NAME)
                .email(EMAIL)
                .role(Role.USER)
                .created_At(LocalDateTime.now())
                .build();

        when(userService.getUserByEmail(EMAIL)).thenReturn(user);

        // Act & Assert
        mockMvc.perform(get("/api/users/by-email")
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value(EMAIL));
    }

    // POST /api/users
    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {

        // Arrange
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .fullName(FULL_NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .role(Role.USER)
                .build();

        User savedUser = User.builder()
                .id(USER_ID)
                .fullName(FULL_NAME)
                .email(EMAIL)
                .role(Role.USER)
                .created_At(LocalDateTime.now())
                .build();

        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.fullName").value(FULL_NAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value(USER_ROLE));
    }
}
