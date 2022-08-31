package com.org.authservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.authservice.exceptions.DependencyException;
import com.org.authservice.exceptions.InvalidInputException;
import com.org.authservice.models.Representation;
import com.org.authservice.models.User;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Slf4j
@Path("/auth-service")
@Api(value = "/auth-service")
@Produces(MediaType.APPLICATION_JSON)
public class AuthServiceResource {
    private final UserService userService;
    private final TokenService tokenService;

    public AuthServiceResource(final UserService userService, final TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @POST
    @Timed
    @Path("/register")
    public Representation<String> registerUser(@QueryParam("email") @NotEmpty @NotNull final String email,
                                               @QueryParam("username") @NotEmpty @NotNull final String username,
                                               @QueryParam("password") @NotEmpty @NotNull final String password) {
        try {
            log.info("Register call with username : " + username + " and email " + email);
            if (userService.isExistingUser(email, username))
                throw new WebApplicationException("User already exists", HttpStatus.BAD_REQUEST_400);
            String userId = userService.createUser(email, username, password);
            String token = tokenService.generateToken(userId);
            log.info("Register successful for username : " + username + " and email " + email);
            return new Representation<String>(HttpStatus.OK_200, token);
        } catch (DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        } catch (InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @GET
    @Timed
    @Path("/login")
    public Representation<String> login(@QueryParam("username") @NotEmpty @NotNull final String username,
                                        @QueryParam("password") @NotEmpty @NotNull final String password) {
        try {
            log.info("Login call with username : " + username);
            Optional<User> user = userService.getRegisteredUser(username, password);
            if (!user.isPresent())
                throw new WebApplicationException("Username or password invalid", HttpStatus.BAD_REQUEST_400);
            String token = tokenService.generateToken(user.get().getId());
            log.info("Login successful with username : " + username);
            return new Representation<String>(HttpStatus.OK_200, token);
        } catch (DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        } catch (InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @GET
    @Timed
    @Path("/lookup")
    public Representation<User> lookup(@QueryParam("token") @NotEmpty @NotNull final String token) {
        try {
            Jws<Claims> jwt = tokenService.parseToken(token);
            log.info("Lookup call with user id : " + jwt.getBody().getId());
            Optional<User> user = userService.getUserById(jwt.getBody().getId());
            if (!user.isPresent())
                throw new WebApplicationException("User not found for the token", HttpStatus.BAD_REQUEST_400);
            log.info("Lookup call successful with user id : " + jwt.getBody().getId());
            return new Representation<User>(HttpStatus.OK_200, user.get());
        } catch (DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        } catch (InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @DELETE
    @Timed
    @Path("/delete")
    public Representation<String> deleteUser(@QueryParam("token") @NotEmpty @NotNull final String token) {
        try {
            Jws<Claims> jwt = tokenService.parseToken(token);
            log.info("Delete call with user id : " + jwt.getBody().getId());
            userService.deleteUserById(jwt.getBody().getId());
            log.info("Delete call successful with user id : " + jwt.getBody().getId());
            return new Representation<String>(HttpStatus.OK_200, "Deletion successful");
        } catch (DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        } catch (InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }
}