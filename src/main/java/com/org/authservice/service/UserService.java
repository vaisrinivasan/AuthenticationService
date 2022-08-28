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

    public Optional<UUID> createUser(String email, String username, String password) {
        final UUID userId = UUID.randomUUID();
        int isCreated = userDao.createUser(new User(userId.toString(), email, username, password));
        return isCreated != -1 ? Optional.of(userId) : Optional.empty();
    }
}
