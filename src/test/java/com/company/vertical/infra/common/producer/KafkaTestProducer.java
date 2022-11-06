package com.company.vertical.infra.common.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaTestProducer {

  @Autowired
  protected KafkaTemplate<String, String> kafkaTemplate;

  public void publish(final String topic, final String event) {
    try {
      this.kafkaTemplate.send(topic, event);
      log.info("Event is published: Event {}", event);
    } catch (final Exception e) {
      log.info("Event cannot be published due to error: Event {}", event, e);
    }
  }
}