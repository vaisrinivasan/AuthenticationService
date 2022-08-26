package com.org.authservice.resources;

import com.codahale.metrics.annotation.Timed;
import com.org.authservice.api.Saying;
import com.org.authservice.models.SampleEntry;
import com.org.authservice.service.SampleService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Path("/auth-service")
@Produces(MediaType.APPLICATION_JSON)
public class AuthServiceResource {
    private final String template;
    private final String defaultName;
    private final AtomicLong counter;
    private final SampleService sampleService;

    public AuthServiceResource(String template, String defaultName, SampleService sampleService) {
        this.template = template;
        this.defaultName = defaultName;
        this.counter = new AtomicLong();
        this.sampleService = sampleService;
    }

    @GET
    @Timed
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String value = String.format(template, name.orElse(defaultName));
        sampleService.create(new SampleEntry(new Random().nextInt(1000), name.orElse(defaultName)));
        return new Saying(counter.incrementAndGet(), value);
    }
}