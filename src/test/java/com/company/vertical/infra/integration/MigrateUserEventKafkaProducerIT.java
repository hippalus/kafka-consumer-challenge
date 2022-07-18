package com.company.vertical.infra.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.vertical.IT;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.company.vertical.infra.adapters.enrichment.event.MigrateUserEventKafkaConsumer;
import com.company.vertical.infra.adapters.migration.event.MigrateUserEventKafkaProducer;
import com.company.vertical.infra.common.consumer.MigrateUserEventKafkaTestConsumer;
import io.cloudevents.CloudEvent;
import io.cloudevents.SpecVersion;
import java.net.URI;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IT
class MigrateUserEventKafkaProducerIT {

  @Autowired
  MigrateUserEventKafkaProducer migrateUserEventKafkaProducer;

  @Autowired
  MigrateUserEventKafkaConsumer migrateUserEventKafkaConsumer;

  @Autowired
  MigrateUserEventKafkaTestConsumer migrateUserEventKafkaTestConsumer;


  @Test
  void should_send_event() {
    // given
    final MigrateUser migrateUserEvent = new MigrateUser(1L);

    // when
    this.migrateUserEventKafkaProducer.publish(migrateUserEvent);

    // then
    this.migrateUserEventKafkaTestConsumer.wait(5, 1);

    final var consumerRecords = this.migrateUserEventKafkaTestConsumer.popAll();
    assertThat(consumerRecords).hasSize(1);

    final ConsumerRecord<String, String> consumerRecord = consumerRecords.get(0);
    assertThat(consumerRecord).isNotNull();

    final CloudEvent cloudEvent = this.migrateUserEventKafkaConsumer.toCloudEvent(consumerRecord.value());
    assertThat(cloudEvent.getTime()).isNotNull();
    assertThat(cloudEvent.getId()).isNotNull();
    assertThat(cloudEvent)
        .returns(URI.create("http://localhost"), CloudEvent::getSource)
        .returns("com.company.vertical.domain.user.migration.event.MigrateUser", CloudEvent::getType)
        .returns("application/json", CloudEvent::getDataContentType)
        .returns("MigrateUser", CloudEvent::getSubject)
        .returns(SpecVersion.V1, CloudEvent::getSpecVersion);

    final MigrateUser receivedEventData = this.migrateUserEventKafkaConsumer.getMigrateUserEvent(cloudEvent);
    assertThat(receivedEventData).isEqualTo(migrateUserEvent);
  }

}
