package com.company.vertical.infra.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.client-name}")
  private String clientName;

  @Value("${spring.kafka.client-secret}")
  private String clientSecret;

  @Value("${spring.kafka.security.protocol}")
  private String protocol;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    final Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put("security.protocol", this.protocol);
    configProps.put("client.dns.lookup", ClientDnsLookup.USE_ALL_DNS_IPS.toString());
    configProps.put("sasl.mechanism", "PLAIN");
    configProps.put("sasl.jaas.config",
        "org.apache.kafka.common.security.plain.PlainLoginModule   required username='" + this.clientName + "'   password='"
            + this.clientSecret + "';");

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(this.producerFactory());
  }
}
