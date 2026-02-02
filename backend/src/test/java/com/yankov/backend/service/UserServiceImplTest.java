package com.yankov.backend.service;

import com.yankov.backend.exception.UserAlreadyExistsException;
import com.yankov.backend.exception.UserNotFoundException;
import com.yankov.backend.model.User;
import com.yankov.backend.repository.UserRepository;
import com.yankov.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "test@example.com";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FULL_NAME = "Test User";

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(USER_ID)
                .email(USER_EMAIL)
                .fullName(USER_FULL_NAME)
                .password(USER_PASSWORD)
                .build();
    }

    // Creates a new user when the email does not already exist
    @Test
    void createUser_shouldSaveUser_whenEmailDoesNotExist() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn(USER_PASSWORD);

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.createUser(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(userRepository).save(user);
    }

    // Prevents creating a user if the email is already registered
    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).findByEmail(USER_EMAIL);
        verify(userRepository, never()).save(any());
    }

    // Retrieves a user by email when it exists
    @Test
    void getUserByEmail_shouldReturnUser_whenFound() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(USER_EMAIL);

        assertThat(result).isEqualTo(user);
        verify(userRepository).findByEmail(USER_EMAIL);
    }

    // Throws an exception when trying to retrieve a user by a non-existing email
    @Test
    void getUserByEmail_shouldThrowException_whenNotFound() {

        when(userRepository.findByEmail(USER_EMAIL))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(USER_EMAIL))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByEmail(USER_EMAIL);
    }

    // Retrieves a user by ID when it exists
    @Test
    void getUserById_shouldReturnUser_whenFound() {

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(USER_ID);

        assertThat(result).isEqualTo(user);
        verify(userRepository).findById(USER_ID);
    }

    // Throws an exception when trying to retrieve a user by a non-existing ID
    @Test
    void getUserById_shouldThrowException_whenNotFound() {

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(USER_ID);
    }
}
