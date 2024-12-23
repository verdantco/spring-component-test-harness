package com.verdantco.componenttest.jpa;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@Component
public class JPAPostProcessor implements BeanPostProcessor {

    private static List<JdbcDatabaseContainer<?>> dbs = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws UnsupportedOperationException {
        if(bean instanceof DataSourceProperties) {
            var props = (DataSourceProperties) bean;

            logger.info("DataSource detected: {}", props.getName());

            switch (props.getDriverClassName()) {
                case "org.postgresql.Driver" -> props = initPostgres(props);
                default -> throw new UnsupportedOperationException("Driver " + props.getDriverClassName() + " not supported");
            }

            return props;
        }

        return bean;
    }

    @SuppressWarnings("resource")
    private DataSourceProperties initPostgres(DataSourceProperties props) {
        var postgres = new PostgreSQLContainer<>("postgres:latest")
                .withInitScript("sql/" + props.getName() + ".sql")
                .withUsername(props.getUsername())
                .withPassword(props.getPassword());
        
        postgres.start();
        dbs.add(postgres);

        logger.info("Successfully created postgreSQL DB for DataSource {}", props.getName());

        props.setUrl(postgres.getJdbcUrl());
        return props;
    }

}
