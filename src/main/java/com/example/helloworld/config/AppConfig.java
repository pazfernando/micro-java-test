package com.example.helloworld.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

@Configuration
public class AppConfig {
    @Autowired
    private MongoClient mongoClient;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(this.mongoClient, "mongo");
    }
}
