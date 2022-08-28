package com.org.authservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.authservice.exceptions.InvalidUserException;
import com.org.authservice.exceptions.UserAlreadyExistsException;
import com.org.authservice.exceptions.UserNotCreatedException;
import com.org.authservice.exceptions.UserNotRegisteredException;
import com.org.authservice.models.Representation;
import com.org.authservice.models.User;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

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
        if(userService.isExistingUser(email, username, password))
            throw new UserAlreadyExistsException("User already exists");
        Optional<String> userId = userService.createUser(email,username, password);
        if(!userId.isPresent())
            throw new UserNotCreatedException("User not created");
        else {
            String token = tokenService.generateToken(userId.get());
            return new Representation<String>(HttpStatus.OK_200, token);
        }
    }

    @GET
    @Timed
    @Path("/login")
    public Representation<String> login(@QueryParam("username") @NotEmpty @NotNull String username,
                                                     @QueryParam("password") @NotEmpty @NotNull String password) {
        Optional<User> user = userService.getRegisteredUser(username, password);
        if(!user.isPresent())
            throw new UserNotRegisteredException("username or password invalid");
        else {
            String token = tokenService.generateToken(user.get().getId());
            return new Representation<String>(HttpStatus.OK_200, token);
        }
    }

    @GET
    @Timed
    @Path("/lookup")
    public Representation<User> lookup(@QueryParam("token") @NotEmpty @NotNull String token) {
        Jws<Claims> jwt = tokenService.parseToken(token);
        Optional<User> user = userService.getUserById(jwt.getBody().getId());
        if(!user.isPresent())
            throw new InvalidUserException("User Id invalid or user not found");
        else
            return new Representation<User>(HttpStatus.OK_200, user.get());
    }

    @DELETE
    @Timed
    @Path("/delete")
    public Representation<String> deleteUser(@QueryParam("token") @NotEmpty @NotNull String token) {
        Jws<Claims> jwt = tokenService.parseToken(token);
        int status = userService.deleteUserById(jwt.getBody().getId());
        if(status == 1)
            return new Representation<String>(HttpStatus.OK_200, "Deletion successful");
        else if(status == 0)
            throw new InvalidUserException("User Id invalid or user not found");
        else
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
    }
}