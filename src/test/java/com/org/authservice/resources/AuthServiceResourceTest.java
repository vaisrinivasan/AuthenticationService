package com.org.authservice.resources;

import com.org.authservice.dao.UserDao;
import com.org.authservice.models.Representation;
import com.org.authservice.models.User;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.UUID;

import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(DropwizardExtensionsSupport.class)
public class AuthServiceResourceTest {

    private static final String TOKEN_SECRET = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";
    private static final String ID = "6877dae9-827b-4991-81dc-c34ef86bd89d";
    private static final String USERNAME = "abc";
    private static final String EMAIL = "abc@gmail.com";
    private static final String PASSWORD = "abc";

    private static final UserDao userDao = mock(UserDao.class);
    private static final UserService userService = new UserService(userDao);
    private static final TokenService tokenService = new TokenService(TOKEN_SECRET);
    private static final ResourceExtension EXT = ResourceExtension.builder()
            .addResource(new AuthServiceResource(userService, tokenService))
            .build();
    private User user;

    @BeforeEach
    public void setup() {
        user = new User(ID,EMAIL, USERNAME, PASSWORD);
    }

    @AfterEach
    public void tearDown() {
        reset(userDao);
    }

    @Test
    public void testRegisterUserExists() {
        when(userDao.getUser(EMAIL, USERNAME)).thenReturn(user);
        Entity<?> entity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = EXT.target("/auth-service/register")
                .queryParam("email", "abc@gmail.com")
                .queryParam("username", "abc")
                .queryParam("password", "abc")
                .request()
                .post(entity);
        String output = response.readEntity(String.class);
        Assertions.assertTrue(output.contains("500"));
        Assertions.assertTrue(output.contains("User already exists"));
    }

    @Test
    public void testRegisterNewUser() {
        User newUser = createUser();
        Entity<?> entity = Entity.entity(newUser, MediaType.APPLICATION_JSON_TYPE);
        Response response = EXT.target("/auth-service/register")
                .queryParam("email", "abc@gmail.com")
                .queryParam("username", "abc")
                .queryParam("password", "abc")
                .request()
                .post(entity);
        Representation<String> output = response.readEntity(Representation.class);
        Assertions.assertEquals(200, output.getCode());
        Assertions.assertEquals(3, output.getData().split("\\.").length);
    }

    @Test
    public void testLoginInvalidUsername() {
        when(userDao.getRegisteredUser(USERNAME, PASSWORD)).thenReturn(null);
        Response response = EXT.target("/auth-service/login")
                .queryParam("username", "abc")
                .queryParam("password", "abc")
                .request()
                .get();
        String output = response.readEntity(String.class);
        Assertions.assertTrue(output.contains("500"));
        Assertions.assertTrue(output.contains("Username or password invalid"));
    }

    @Test
    public void testLoginValid() {
        when(userDao.getRegisteredUser(USERNAME, PASSWORD)).thenReturn(user);
        Response response = EXT.target("/auth-service/login")
                .queryParam("username", "abc")
                .queryParam("password", "abc")
                .request()
                .get();
        Representation<String> output = response.readEntity(Representation.class);
        Assertions.assertEquals(200, output.getCode());
        Assertions.assertEquals(3, output.getData().split("\\.").length);
    }

    @Test
    public void testLookupUserNotFound() {
        when(userDao.getUserById(any())).thenReturn(null);
        String token = tokenService.generateToken(UUID.randomUUID().toString());
        Response response = EXT.target("/auth-service/lookup")
                .queryParam("token", token)
                .request()
                .get();
        String output = response.readEntity(String.class);
        System.out.println(output);
        Assertions.assertTrue(output.contains("500"));
        Assertions.assertTrue(output.contains("User not found for the token"));
    }

    @Test
    public void testLookupUserSuccessful() {
        when(userDao.getUserById(any())).thenReturn(user);
        String token = tokenService.generateToken(UUID.randomUUID().toString());
        Response response = EXT.target("/auth-service/lookup")
                .queryParam("token", token)
                .request()
                .get();
        String output = response.readEntity(String.class);
        Assertions.assertTrue(output.contains("200"));
        Assertions.assertTrue(output.contains(ID));
        Assertions.assertTrue(output.contains(EMAIL));
        Assertions.assertTrue(output.contains(USERNAME));
    }

    @Test
    public void testDeleteSuccessful() {
        when(userDao.deleteUserById(any())).thenReturn(1);
        String token = tokenService.generateToken(UUID.randomUUID().toString());
        Response response = EXT.target("/auth-service/delete")
                .queryParam("token", token)
                .request()
                .delete();
        Representation<String> output = response.readEntity(Representation.class);
        Assertions.assertEquals(200,output.getCode());
        Assertions.assertEquals("Deletion successful", output.getData());
    }

    @Test
    public void testDeleteUnSuccessful() {
        when(userDao.deleteUserById(any())).thenReturn(0);
        String token = tokenService.generateToken(UUID.randomUUID().toString());
        Response response = EXT.target("/auth-service/delete")
                .queryParam("token", token)
                .request()
                .delete();
        String output = response.readEntity(String.class);
        Assertions.assertTrue(output.contains("400"));
        Assertions.assertTrue(output.contains("User Id invalid or user not found"));
    }

    private User createUser() {
        String new_id = UUID.randomUUID().toString();
        String new_username = "new";
        String new_email = "new@gmail.com";
        String new_password = "new";
        return new User(new_id, new_email, new_username, new_password);
    }
}