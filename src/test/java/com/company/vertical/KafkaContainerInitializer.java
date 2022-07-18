package com.company.vertical;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Profile("integrationTest")
public class KafkaContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @SuppressWarnings("resource")
  @Override
  public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {

    final KafkaContainer kafkaContainer = this.buildBrokerInstance();
    kafkaContainer.start();

    configurableApplicationContext.addApplicationListener(
        (ApplicationListener<ContextClosedEvent>) event -> kafkaContainer.stop());

    TestPropertyValues.of("spring.kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers())
        .applyTo(configurableApplicationContext.getEnvironment());
  }

  @SuppressWarnings("resource")
  public KafkaContainer buildBrokerInstance() {
    final Map<String, String> env = new LinkedHashMap<>();
    env.put("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:SASL_PLAINTEXT");

    env.put("KAFKA_LISTENER_NAME_PLAINTEXT_SASL_ENABLED_MECHANISMS", "PLAIN");

    env.put("KAFKA_LISTENER_NAME_PLAINTEXT_PLAIN_SASL_JAAS_CONFIG",
        """
            org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="admin-secret" user_admin="admin-secret" user_kafka-consumer="secret-password";""");

    env.put("KAFKA_SASL_JAAS_CONFIG", "org.apache.kafka.common.security.plain.PlainLoginModule required " +
        "username=\"admin\" " +
        "password=\"admin-secret\";");

    return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.2"))
        .withEmbeddedZookeeper()
        .withStartupAttempts(1)
        .withEnv(env);
  }
}
