package com.org.authservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.authservice.exceptions.*;
import com.org.authservice.models.Representation;
import com.org.authservice.models.User;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Slf4j
@Path("/auth-service")
@Produces(MediaType.APPLICATION_JSON)
public class AuthServiceResource {
    private final UserService userService;
    private final TokenService tokenService;

    public AuthServiceResource(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @POST
    @Timed
    @Path("/register")
    public Representation<String> registerUser(@QueryParam("email") @NotEmpty @NotNull String email,
                                                            @QueryParam("username") @NotEmpty @NotNull String username,
                                                            @QueryParam("password") @NotEmpty @NotNull String password) {
        try {
            if (userService.isExistingUser(email, username))
                throw new WebApplicationException("User already exists", HttpStatus.BAD_REQUEST_400);
            String userId = userService.createUser(email, username, password);
            String token = tokenService.generateToken(userId);
            return new Representation<String>(HttpStatus.OK_200, token);
        }
        catch(DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        }
        catch(InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        }
        catch(Exception exception) {
            log.error(exception.getMessage(),exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @GET
    @Timed
    @Path("/login")
    public Representation<String> login(@QueryParam("username") @NotEmpty @NotNull String username,
                                                     @QueryParam("password") @NotEmpty @NotNull String password) {
        try {
            Optional<User> user = userService.getRegisteredUser(username, password);
            if (!user.isPresent())
                throw new WebApplicationException("Username or password invalid", HttpStatus.BAD_REQUEST_400);
            String token = tokenService.generateToken(user.get().getId());
            return new Representation<String>(HttpStatus.OK_200, token);
        }
        catch(DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        }
        catch(InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        }
        catch(Exception exception) {
            log.error(exception.getMessage(),exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @GET
    @Timed
    @Path("/lookup")
    public Representation<User> lookup(@QueryParam("token") @NotEmpty @NotNull String token) {
        try {
            Jws<Claims> jwt = tokenService.parseToken(token);
            Optional<User> user = userService.getUserById(jwt.getBody().getId());
            if (!user.isPresent())
                throw new WebApplicationException("User not found for the token", HttpStatus.BAD_REQUEST_400);
            return new Representation<User>(HttpStatus.OK_200, user.get());
        }
        catch(DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        }
        catch(InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        }
        catch(Exception exception) {
            log.error(exception.getMessage(),exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @DELETE
    @Timed
    @Path("/delete")
    public Representation<String> deleteUser(@QueryParam("token") @NotEmpty @NotNull String token) {
        try {
            Jws<Claims> jwt = tokenService.parseToken(token);
            int status = userService.deleteUserById(jwt.getBody().getId());
            if (status == 1)
                return new Representation<String>(HttpStatus.OK_200, "Deletion successful");
            else
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        catch(DependencyException dependencyException) {
            log.error(dependencyException.getMessage(), dependencyException);
            throw new WebApplicationException(dependencyException.getMessage(), HttpStatus.SERVICE_UNAVAILABLE_503);
        }
        catch(InvalidInputException invalidInputException) {
            log.error(invalidInputException.getMessage(), invalidInputException);
            throw new WebApplicationException(invalidInputException.getMessage(), HttpStatus.BAD_REQUEST_400);
        }
        catch(Exception exception) {
            log.error(exception.getMessage(),exception);
            throw new WebApplicationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }
}