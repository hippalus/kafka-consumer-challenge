package com.company.vertical.infra.integration;

import com.company.vertical.AbstractIT;
import com.company.vertical.IT;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.company.vertical.infra.adapters.migration.event.MigrateUserEventKafkaProducer;
import com.company.vertical.infra.common.EventAssertion;
import com.company.vertical.infra.common.producer.KafkaTestProducer;
import com.company.vertical.infra.common.usecase.FakeEnrichUserUseCaseHandler;
import io.cloudevents.CloudEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

@IT
@TestPropertySource(properties = "spring.kafka.consumer.auto-startup=true")
class MigrateUserEventKafkaConsumerIT extends AbstractIT {

  final EventAssertion<MigrateUser, EnrichedUser> enrichedUserEventAssertion = new EventAssertion<>();
  @Autowired
  KafkaTestProducer kafkaTestProducer;
  @Autowired
  MigrateUserEventKafkaProducer migrateUserEventKafkaProducer;
  @Autowired
  FakeEnrichUserUseCaseHandler fakeEnrichUserUseCaseHandler;

  @Test
  void shouldReceiveMigrateUserEvent() {

    final MigrateUser migrateUser = new MigrateUser(1L);

    final CloudEvent cloudEvent = this.migrateUserEventKafkaProducer.createCloudEvent(migrateUser);
    final String event = this.migrateUserEventKafkaProducer.toStr(cloudEvent);

    this.kafkaTestProducer.publish("com.company.vertical.migrate-user", event);

    this.enrichedUserEventAssertion.assertEventProcessed(
        60,
        migrateUser,
        () -> this.fakeEnrichUserUseCaseHandler.getEnrichedUser()
    );

  }

}