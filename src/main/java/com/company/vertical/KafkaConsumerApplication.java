package com.company.vertical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry(proxyTargetClass = true)
@SpringBootApplication
public class KafkaConsumerApplication {

  public static void main(final String[] args) {
    SpringApplication.run(KafkaConsumerApplication.class, args);
  }

}
