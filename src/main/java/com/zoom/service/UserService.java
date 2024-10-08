package com.zoom.service;

import com.zoom.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(User user);

    User findUserByEmail(String email);

    User getCurrentUser();

    User getUserByEmail(String email);

    void updateUser(User user);
}

