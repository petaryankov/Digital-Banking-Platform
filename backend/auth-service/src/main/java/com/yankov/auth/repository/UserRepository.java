package com.yankov.auth.repository;

import com.yankov.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @NonNull
    List<User> findAll();
}
