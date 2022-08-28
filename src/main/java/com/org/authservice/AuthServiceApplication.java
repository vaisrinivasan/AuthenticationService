package com.org.authservice;

import com.org.authservice.dao.UserDao;
import com.org.authservice.resources.AuthServiceResource;
import com.org.authservice.service.TokenService;
import com.org.authservice.service.UserService;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;

public class AuthServiceApplication extends Application<AuthServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new AuthServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "auth-service";
    }

    @Override
    public void initialize(Bootstrap<AuthServiceConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(AuthServiceConfiguration configuration, Environment environment) throws UnsupportedEncodingException {
        final DataSource dataSource =
                configuration.getDataSourceFactory().build(environment.metrics(), "postgresql");
        final DBI dbi = new DBI(dataSource);
        final UserDao userDao = dbi.onDemand(UserDao.class);
        final UserService userService = new UserService(userDao);
        final TokenService tokenService = new TokenService(configuration.getJwtTokenSecret());
        final AuthServiceResource resource = new AuthServiceResource(userService, tokenService);
        //Todo - Add a health check
        //final TemplateHealthCheck healthCheck =new TemplateHealthCheck(configuration.getTemplate());
        //environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}
