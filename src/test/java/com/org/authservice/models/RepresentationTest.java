package com.org.authservice.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Assert;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;

public class RepresentationTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private static final String USER_JSON = "fixtures/user.json";
    private static final String TEST_USER_ID = "6877dae9-827b-4991-81dc-c34ef86bd89d";
    private static final String TEST_USER_USERNAME = "abc";
    private static final String TEST_USER_EMAIL = "abc@gmail.com";
    private static final String TEST_USER_PASSWORD = "$2a$08$gQvprbenfs77jFJIUJwmteRMqICJI2hfWg4L03DkPU0AaAzI5qrrC";

    @Test
    public void serializesToJSON() throws Exception {
        final User user = new User(TEST_USER_ID, TEST_USER_EMAIL, TEST_USER_USERNAME, TEST_USER_PASSWORD);
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture(USER_JSON), User.class));
        Assert.assertEquals(MAPPER.writeValueAsString(user), expected);
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        final User user = new User(TEST_USER_ID, TEST_USER_EMAIL, TEST_USER_USERNAME, TEST_USER_PASSWORD);
        Assert.assertEquals(MAPPER.readValue(fixture(USER_JSON), User.class).getId(), user.getId());
        Assert.assertEquals(MAPPER.readValue(fixture(USER_JSON), User.class).getEmail(), user.getEmail());
        Assert.assertEquals(MAPPER.readValue(fixture(USER_JSON), User.class).getUsername(), user.getUsername());
    }
}