package com.yankov.backend.service;

import com.yankov.backend.model.User;

import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserById(Long id);
}
