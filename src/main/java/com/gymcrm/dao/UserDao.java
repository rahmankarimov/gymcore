package com.gymcrm.dao;

import com.gymcrm.domain.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsername(String username);
}
