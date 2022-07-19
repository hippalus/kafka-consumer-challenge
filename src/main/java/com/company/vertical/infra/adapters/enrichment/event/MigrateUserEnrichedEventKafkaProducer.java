package com.company.vertical.infra.adapters.enrichment.event;

import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.enrichment.port.MigrateUserEnrichedEventPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "kafka.enabled", matchIfMissing = true)
public class MigrateUserEnrichedEventKafkaProducer implements MigrateUserEnrichedEventPort {

  private static final String EVENT_SOURCE = "http://localhost";
  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> kafkaTemplate;
  @Value("${spring.kafka.topic}")
  private String topic;

  @Override
  public void publish(final MigrateUserEnriched event) {
    final CloudEvent cloudEvent = this.createCloudEvent(event);
    final String cloudEventStr = this.toStr(cloudEvent);
    this.kafkaTemplate.send(this.topic, cloudEventStr);
  }

  private CloudEvent createCloudEvent(final MigrateUserEnriched migrateUserEnriched) {
    return CloudEventBuilder.v1()
        .withType(migrateUserEnriched.getClass().getCanonicalName())
        .withSubject(migrateUserEnriched.getClass().getSimpleName())
        .withSource(URI.create(EVENT_SOURCE))
        .withId(UUID.randomUUID().toString())
        .withDataContentType(MediaType.APPLICATION_JSON.toString())
        .withData(PojoCloudEventData.wrap(migrateUserEnriched, this.objectMapper::writeValueAsBytes))
        .build();
  }

  private String toStr(final CloudEvent enrichedEvent) {
    return new String(
        Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE))
            .serialize(enrichedEvent)
    );
  }

}
