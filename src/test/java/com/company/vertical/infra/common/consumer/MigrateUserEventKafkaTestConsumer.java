package com.company.vertical.infra.common.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MigrateUserEventKafkaTestConsumer extends AbstractEventKafkaTestConsumer {

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Override
  @KafkaListener(
      id = "MigrateUserEventKafkaTestConsumer",
      topics = "${spring.kafka.topic}",
      autoStartup = "false",
      containerFactory = "kafkaListenerContainerFactory",
      groupId = "migrateUserEventKafkaTestConsumer"
  )
  public void consume(@Payload final ConsumerRecord<String, String> event) {
    this.consumerInternal(event);
  }

  @Override
  public void start() {
    this.kafkaListenerEndpointRegistry.getListenerContainer("MigrateUserEventKafkaTestConsumer").start();
  }

  @Override
  public void stop() {
    this.kafkaListenerEndpointRegistry.getListenerContainer("MigrateUserEventKafkaTestConsumer").stop();
  }


}
