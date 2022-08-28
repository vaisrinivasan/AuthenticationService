package com.org.authservice.service;

import com.org.authservice.dao.UserDao;
import com.org.authservice.models.User;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserService {
    private final UserDao userDao;

    public boolean isExistingUser(String email, String username, String password) {
        User user = userDao.getUser(email, username, password);
        return user != null;
    }

    public Optional<String> createUser(String email, String username, String password) {
        final String userId = UUID.randomUUID().toString();
        int isCreated = userDao.createUser(new User(userId, email, username, password));
        return isCreated != -1 ? Optional.of(userId) : Optional.empty();
    }

    public Optional<User> getRegisteredUser(String username, String password) {
        User user = userDao.getRegisteredUser(username, password);
        return user == null ? Optional.empty() : Optional.of(user);
    }

    public Optional<User> getUserById(String id) {
        User user = userDao.getUserById(id);
        return user == null ? Optional.empty() : Optional.of(user);
    }

    public int deleteUserById(String id) {
        return userDao.deleteUserById(id);
    }
}
