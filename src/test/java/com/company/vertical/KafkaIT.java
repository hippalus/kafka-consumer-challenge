package com.company.vertical;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@Retention(RetentionPolicy.RUNTIME)
@TestPropertySource(properties = "kafka.enabled=true")
@ContextConfiguration(initializers = KafkaContainerInitializer.class)
public @interface KafkaIT {

}
