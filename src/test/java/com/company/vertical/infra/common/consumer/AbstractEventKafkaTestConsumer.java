package com.company.vertical.infra.common.consumer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.handler.annotation.Payload;

@Slf4j
public abstract class AbstractEventKafkaTestConsumer {

  private Deque<ConsumerRecord<String, String>> eventsQueue;

  public abstract void consume(@Payload ConsumerRecord<String, String> event);

  public abstract void start();

  public abstract void stop();

  protected void consumerInternal(final ConsumerRecord<String, String> event) {
    log.info("Event received:: {}", event);

    this.initializeEventQueue();
    this.keepEvent(event);

    log.info("Event added to the kafka event list as {}th entry, event: {}", this.queueSize(), event);
  }

  public void wait(final int maxWaitInSec, final int expectedEventSize) {
    synchronized (this) {
      this.initializeEventQueue();
      try {
        final LocalDateTime startTime = LocalDateTime.now();
        int checkCount = 0;
        while (true) {
          if (!(checkCount++ < maxWaitInSec)) {
            break;
          }
          if (this.eventsQueue.size() >= expectedEventSize) {
            break;
          }
          this.sleepOneSecond();
        }
        final LocalDateTime endTime = LocalDateTime.now();
        log.info("Tried {} seconds. Expected event count is {} and actual event size is {}",
            Duration.between(startTime, endTime).toSeconds(), expectedEventSize, this.eventsQueue.size());
      } catch (final Exception e) {
        log.info("Expected event count is {} and actual event size is {}" + e, expectedEventSize,
            Objects.isNull(this.eventsQueue) ? 0 : this.eventsQueue.size());
      }
    }
  }

  protected void initializeEventQueue() {
    if (Objects.isNull(this.eventsQueue)) {
      this.eventsQueue = new ConcurrentLinkedDeque<>();
    }
  }

  protected void keepEvent(final ConsumerRecord<String, String> event) {
    this.eventsQueue.add(event);
  }

  protected int queueSize() {
    return this.eventsQueue.size();
  }

  private void sleepOneSecond() {
    try {
      TimeUnit.MILLISECONDS.sleep(10000);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void reset() {
    this.eventsQueue = null;
  }

  public ConsumerRecord<String, String> pop() {
    final ConsumerRecord<String, String> event = this.eventsQueue.pollFirst();
    log.info("Last event from the consumed list is popped for validation. Event: {}", event);
    return event;
  }

  public List<ConsumerRecord<String, String>> popAll() {
    synchronized (this) {
      if (Objects.isNull(this.eventsQueue)) {
        return List.of();
      }

      final List<ConsumerRecord<String, String>> eventsAsList = new ArrayList<>(this.eventsQueue);
      log.info("All {} events from the consumed list is popped for validation.", this.eventsQueue.size());
      this.eventsQueue.clear();

      return eventsAsList;
    }
  }
}
