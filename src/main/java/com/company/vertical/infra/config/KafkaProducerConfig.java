package com.company.vertical.infra.config;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

  private final KafkaProperties kafkaProperties;

  @Bean
  public ProducerFactory<String, String> producerFactory() {
    final Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaProperties.getBootstrapServers());
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, Boolean.TRUE);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put("security.protocol", this.kafkaProperties.getProtocol());
    configProps.put("client.dns.lookup", ClientDnsLookup.USE_ALL_DNS_IPS.toString());
    configProps.put("sasl.mechanism", "PLAIN");
    configProps.put("sasl.jaas.config",
        "org.apache.kafka.common.security.plain.PlainLoginModule   required username='" + this.kafkaProperties.getClientName()
            + "'   password='" + this.kafkaProperties.getClientSecret() + "';");

    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, String> kafkaTemplate() {
    return new KafkaTemplate<>(this.producerFactory());
  }
}
