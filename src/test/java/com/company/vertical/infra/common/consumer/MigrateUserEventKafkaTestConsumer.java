package com.company.vertical.infra.common.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MigrateUserEventKafkaTestConsumer extends AbstractEventKafkaTestConsumer {

  @Override
  @KafkaListener(topics = "${spring.kafka.topic}",
      autoStartup = "true",
      containerFactory = "kafkaListenerContainerFactory",
      groupId = "migrateUserEventKafkaTestConsumer"
  )
  public void consume(@Payload final ConsumerRecord<String, String> event) {
    this.consumerInternal(event);
  }
}
