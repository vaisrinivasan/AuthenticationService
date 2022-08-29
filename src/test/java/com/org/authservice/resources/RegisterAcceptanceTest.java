package com.org.authservice.resources;

import com.org.authservice.AuthServiceApplication;
import com.org.authservice.AuthServiceConfiguration;
import com.org.authservice.models.User;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@ExtendWith(DropwizardExtensionsSupport.class)
public class RegisterAcceptanceTest {
    private static DropwizardAppExtension<AuthServiceConfiguration> EXT = new DropwizardAppExtension<>(
            AuthServiceApplication.class,
            ResourceHelpers.resourceFilePath("/auth-service.yml")
    );

    @Test
    void testRegister() {
        Client client = EXT.client();
        User user = createUser();
        Entity<?> entity = Entity.entity(user, MediaType.APPLICATION_JSON_TYPE);
        Response response = client.target("/auth-service/register")
                .queryParam("email", "abc@gmail.com")
                .queryParam("username", "abc")
                .queryParam("password", "abc")
                .request()
                .post(entity);

        Assertions.assertTrue(response.getStatus() == 200);
    }

    private User createUser() {
        String new_id = UUID.randomUUID().toString();
        String new_username = "abc";
        String new_email = "abc@gmail.com";
        String new_password = "abc";
        return new User(new_id, new_email, new_username, new_password);
    }
}
