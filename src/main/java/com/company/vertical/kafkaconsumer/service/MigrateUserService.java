package com.company.vertical.kafkaconsumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.vertical.events.MigrateUser;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.data.PojoCloudEventData;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class MigrateUserService {

    private static Logger logger = LoggerFactory.getLogger(MigrateUserService.class);
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static String EVENT_SOURCE = "http://localhost";

    @Value("${spring.kafka.topic}")
    private String topic;

    private KafkaTemplate<String, String> producer;

    private ArrayList<MigrateUser> migratedUsers;

    @Autowired
    public MigrateUserService(KafkaTemplate<String, String> producer){
        this.migratedUsers = new ArrayList();
        this.producer = producer;
    }

    public void migrateUserById(long userId) {
        MigrateUser user = new MigrateUser();
        user.setUserId(userId);
        CloudEvent event = createCloudEvent(user);
        this.migratedUsers.add(user);
        String cloudEvent = new String(EventFormatProvider.getInstance().resolveFormat(JsonFormat.CONTENT_TYPE).serialize(event));
        logger.info("CloudEvent: {}", cloudEvent);
        producer.send(topic, cloudEvent);
    }

    private CloudEvent createCloudEvent(MigrateUser user) {
        return CloudEventBuilder.v1()
                .withType(user.getClass().getCanonicalName())
                .withSubject(user.getClass().getSimpleName())
                .withSource(URI.create(MigrateUserService.EVENT_SOURCE))
                .withId(UUID.randomUUID().toString())
                .withDataContentType(MediaType.APPLICATION_JSON.toString())
                .withData(PojoCloudEventData.wrap(user, mapper::writeValueAsBytes))
                .build();
    }
}
