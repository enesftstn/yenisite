package com.autoparts.exchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.autoparts.exchange.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.autoparts.exchange.repository.elasticsearch")
@EnableTransactionManagement
@EnableAsync
public class AutoPartsExchangeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoPartsExchangeApplication.class, args);
    }
}
