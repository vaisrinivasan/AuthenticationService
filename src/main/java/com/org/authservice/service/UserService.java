package com.org.authservice.service;

import com.org.authservice.dao.UserDao;
import com.org.authservice.exceptions.DependencyException;
import com.org.authservice.models.User;
import lombok.AllArgsConstructor;
import org.skife.jdbi.v2.exceptions.DBIException;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserService {
    private final UserDao userDao;

    public boolean isExistingUser(final String email, final String username) {
        try {
            User user = userDao.getUser(email, username);
            return user != null;
        } catch (DBIException e) {
            throw new DependencyException(e);
        }
    }

    public String createUser(final String email, final String username, final String password) {
        try {
            final String userId = UUID.randomUUID().toString();
            userDao.createUser(new User(userId, email, username, password));
            return userId;
        } catch (DBIException e) {
            throw new DependencyException(e);
        }
    }

    public Optional<User> getRegisteredUser(final String username, final String password) {
        try {
            User user = userDao.getRegisteredUser(username, password);
            return user == null ? Optional.empty() : Optional.of(user);
        } catch (DBIException e) {
            throw new DependencyException(e);
        }
    }

    public Optional<User> getUserById(final String id) {
        try {
            User user = userDao.getUserById(id);
            return user == null ? Optional.empty() : Optional.of(user);
        } catch (DBIException e) {
            throw new DependencyException(e);
        }
    }

    public void deleteUserById(final String id) {
        try {
            userDao.deleteUserById(id);
        } catch (DBIException e) {
            throw new DependencyException(e);
        }
    }

    public void performHealthCheck() {
        userDao.getUserById(UUID.randomUUID().toString());
    }
}
