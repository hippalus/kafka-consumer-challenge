package com.company.vertical.infra.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaProperties {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.client-name}")
  private String clientName;

  @Value("${spring.kafka.client-secret}")
  private String clientSecret;

  @Value("${spring.kafka.security.protocol}")
  private String protocol;

  @Value("${spring.kafka.topic}")
  private String topic;

  @Value("${spring.kafka.consumer.group-id}")
  private String consumerGroupId;

  @Value("${spring.kafka.consumer.concurrency}")
  private Integer concurrency;

}
