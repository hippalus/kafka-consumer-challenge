package com.company.vertical.infra.adapters.migration.event;

import com.company.vertical.domain.user.migration.MigrateUserEventPort;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import java.net.URI;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MigrateUserEventKafkaProducer implements MigrateUserEventPort {

  public static final String EVENT_SOURCE = "http://localhost";
  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;
  @Value("${spring.kafka.topic}")
  private String topic;

  @Override
  public void publish(final MigrateUser event) {
    final CloudEvent cloudEvent = this.createCloudEvent(event);
    final String cloudEventStr = this.toStr(cloudEvent);
    this.kafkaTemplate.send(this.topic, cloudEventStr);
  }

  private CloudEvent createCloudEvent(final MigrateUser user) {
    return CloudEventBuilder.v1()
        .withType(user.getClass().getCanonicalName())
        .withSubject(user.getClass().getSimpleName())
        .withSource(URI.create(EVENT_SOURCE))
        .withTime(OffsetDateTime.now(Clock.systemUTC()))
        .withId(UUID.randomUUID().toString())
        .withDataContentType(MediaType.APPLICATION_JSON.toString())
        .withData(PojoCloudEventData.wrap(user, this.objectMapper::writeValueAsBytes))
        .build();

  }

  private String toStr(final CloudEvent migrateUser) {
    return new String(
        Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE))
            .serialize(migrateUser)
    );
  }
}
