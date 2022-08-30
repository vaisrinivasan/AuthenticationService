package com.org.authservice.service;

import com.org.authservice.dao.UserDao;
import com.org.authservice.exceptions.DependencyException;
import com.org.authservice.exceptions.InvalidInputException;
import com.org.authservice.models.User;
import lombok.AllArgsConstructor;
import org.skife.jdbi.v2.exceptions.DBIException;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class UserService {
    private final UserDao userDao;

    public boolean isExistingUser(String email, String username) {
        try {
            User user = userDao.getUser(email, username);
            return user != null;
        }
        catch(DBIException e) {
            throw new DependencyException(e);
        }
    }

    public String createUser(String email, String username, String password) {
        try {
            final String userId = UUID.randomUUID().toString();
            userDao.createUser(new User(userId, email, username, password));
            return userId;
        }
        catch(DBIException e) {
            throw new DependencyException(e);
        }
    }

    public Optional<User> getRegisteredUser(String username, String password) {
        try {
            User user = userDao.getRegisteredUser(username, password);
            return user == null ? Optional.empty() : Optional.of(user);
        }
        catch(DBIException e) {
            throw new DependencyException(e);
        }
    }

    public Optional<User> getUserById(String id) {
        try {
            User user = userDao.getUserById(id);
            return user == null ? Optional.empty() : Optional.of(user);
        }
        catch(DBIException e) {
            throw new DependencyException(e);
        }
    }

    public int deleteUserById(String id) {
        try {
            int status = userDao.deleteUserById(id);
            if(status == 0)
                throw new InvalidInputException("User Id invalid or user not found");
            return status;
        }
        catch(DBIException e) {
            throw new DependencyException(e);
        }
    }

    public String performHealthCheck() {
        try {
            userDao.getUserById(UUID.randomUUID().toString());
        } catch (UnableToObtainConnectionException ex) {
            return ex.getMessage();
        } catch (UnableToExecuteStatementException ex) {
            return ex.getMessage();
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return null;
    }
}
