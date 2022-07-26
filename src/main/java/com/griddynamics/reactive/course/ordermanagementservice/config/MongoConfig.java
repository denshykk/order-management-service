package com.griddynamics.reactive.course.ordermanagementservice.config;

import java.util.Collections;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@EnableConfigurationProperties
@ConfigurationProperties("spring.data.mongodb")
@EnableReactiveMongoRepositories(basePackages = "com.griddynamics.reactive.course.ordermanagementservice.repository")
public class MongoConfig extends AbstractReactiveMongoConfiguration {

  private String username;
  private String password;
  private String database;
  private String host;
  private String port;

  @Override
  protected String getDatabaseName() {
    return this.database;
  }

  @Bean
  public MongoClient mongoClient() {
    final MongoClientSettings mongoClientSettings = MongoClientSettings
        .builder()
        .credential(MongoCredential.createCredential(this.username, this.database, this.password.toCharArray()))
        .applyToClusterSettings(settings -> settings.hosts(Collections.singletonList(new ServerAddress(this.host,
            Integer.parseInt(this.port)))))
        .build();

    return MongoClients.create(mongoClientSettings);
  }

}
