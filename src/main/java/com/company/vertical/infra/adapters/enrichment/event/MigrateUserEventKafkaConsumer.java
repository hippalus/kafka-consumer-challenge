package com.company.vertical.infra.adapters.enrichment.event;

import com.company.vertical.domain.common.usecase.BeanAwareUseCasePublisher;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateUserEventKafkaConsumer implements BeanAwareUseCasePublisher {

  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${spring.kafka.topic}", autoStartup = "${spring.kafka.consumer.auto-startup:false}", containerFactory = "kafkaListenerContainerFactory")
  public void consume(@Payload final ConsumerRecord<String, String> consumerRecord) {
    final CloudEvent cloudEvent = this.toCloudEvent(consumerRecord.value());
    final MigrateUser migrateUserEvent = this.getMigrateUserEvent(cloudEvent);
    this.publish(new EnrichUser(migrateUserEvent.userId()));
  }

  @SneakyThrows
  public MigrateUser getMigrateUserEvent(@NonNull final CloudEvent cloudEvent) {
    final byte[] bytes = Optional.ofNullable(cloudEvent.getData())
        .map(CloudEventData::toBytes)
        .orElseThrow();

    return this.objectMapper.readValue(bytes, MigrateUser.class);
  }

  public CloudEvent toCloudEvent(final String consumerRecordValue) {
    return Objects.requireNonNull(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE))
        .deserialize(consumerRecordValue.getBytes(StandardCharsets.UTF_8));
  }
}
