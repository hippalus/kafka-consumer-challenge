package com.company.vertical.infra.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

  private final KafkaProperties kafkaProperties;

  @Bean
  public KafkaAdmin admin() {
    final Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBootstrapServers());
    props.put("security.protocol", this.kafkaProperties.getProtocol());
    props.put("client.dns.lookup", ClientDnsLookup.USE_ALL_DNS_IPS.toString());
    props.put("sasl.mechanism", "PLAIN");
    props.put("sasl.jaas.config",
        "org.apache.kafka.common.security.plain.PlainLoginModule   required username='" + this.kafkaProperties.getClientName()
            + "'   password='" + this.kafkaProperties.getClientSecret() + "';");
    return new KafkaAdmin(props);
  }

  @Bean
  public NewTopic migrateUserTopic() {
    return TopicBuilder.name(this.kafkaProperties.getTopic())
        .partitions(10)
        .replicas(1)
        .build();
  }
}
