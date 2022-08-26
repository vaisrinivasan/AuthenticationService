package com.org.authservice;

import com.org.authservice.dao.SampleDao;
import com.org.authservice.health.TemplateHealthCheck;
import com.org.authservice.resources.AuthServiceResource;
import com.org.authservice.service.SampleService;
import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.jdbi.v3.core.Jdbi;
import org.skife.jdbi.v2.DBI;

import javax.sql.DataSource;

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
    public void run(AuthServiceConfiguration configuration, Environment environment) {
        final DataSource dataSource =
                configuration.getDataSourceFactory().build(environment.metrics(), "postgresql");
        DBI dbi = new DBI(dataSource);
        final AuthServiceResource resource = new AuthServiceResource(configuration.getTemplate(), configuration.getDefaultName(), dbi.onDemand(SampleService.class));
        final TemplateHealthCheck healthCheck =new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }
}
