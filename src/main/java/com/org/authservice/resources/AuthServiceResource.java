package com.org.authservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.authservice.exceptions.UserAlreadyExistsException;
import com.org.authservice.exceptions.UserNotCreatedException;
import com.org.authservice.exceptions.UserNotRegisteredException;
import com.org.authservice.models.Representation;
import com.org.authservice.models.User;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    public Representation<Map<String, String>> registerUser(@QueryParam("email") @NotEmpty @NotNull String email,
                                                            @QueryParam("username") @NotEmpty @NotNull String username,
                                                            @QueryParam("password") @NotEmpty @NotNull String password) {
        if(userService.isExistingUser(email, username, password))
            throw new UserAlreadyExistsException("User already exists");
        Optional<String> userId = userService.createUser(email,username, password);
        if(!userId.isPresent())
            throw new UserNotCreatedException("User not created");
        else {
            Map<String, String> tokenMap = tokenService.generateToken(userId.get());
            return new Representation<Map<String, String>>(HttpStatus.OK_200, tokenMap);
        }
    }

    @GET
    @Timed
    @Path("/login")
    public Representation<Map<String, String>> login(@QueryParam("username") @NotEmpty @NotNull String username,
                                                     @QueryParam("password") @NotEmpty @NotNull String password) {
        Optional<User> user = userService.getRegisteredUser(username, password);
        if(!user.isPresent())
            throw new UserNotRegisteredException("User not registered");
        else {
            Map<String, String> tokenMap = tokenService.generateToken(user.get().getId());
            return new Representation<Map<String, String>>(HttpStatus.OK_200, tokenMap);
        }
    }

}