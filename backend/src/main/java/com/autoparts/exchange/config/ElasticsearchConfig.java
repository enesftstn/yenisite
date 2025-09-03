package com.autoparts.exchange.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.autoparts.exchange.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUrl;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.Builder builder = ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl.replace("http://", "").replace("https://", ""));

        if (!username.isEmpty() && !password.isEmpty()) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}
