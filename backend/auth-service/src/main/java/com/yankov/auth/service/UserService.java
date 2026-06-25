package com.yankov.auth.service;

import com.yankov.auth.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUserByEmail(String email);

    User getUserById(Long id);

    boolean existsByEmail(String email);

    void activateUser(Long id);

    void deactivateUser(String email);

    List<User> getAllUsers();
}
