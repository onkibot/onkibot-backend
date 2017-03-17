package com.onkibot.backend.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EntityScan("com.onkibot.backend.database.entities")
@EnableJpaRepositories("com.onkibot.backend.database.repositories")
@EnableTransactionManagement
public class RepositoryConfiguration {
}
