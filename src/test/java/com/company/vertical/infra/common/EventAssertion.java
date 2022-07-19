package com.company.vertical.infra.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.company.vertical.domain.common.model.Event;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventAssertion<T extends Event, E> {

  public void assertEventProcessed(final int maxWaitInSec, final T event, final Supplier<? extends E> resultAfterProcessing) {
    try {
      final LocalDateTime startTime = LocalDateTime.now();
      int checkCount = 0;

      E processedModel;
      while (true) {
        processedModel = resultAfterProcessing.get();
        if (!(checkCount++ < maxWaitInSec)) {
          break;
        }
        if (Objects.nonNull(processedModel)) {
          break;
        }
        this.sleepOneSecond();
      }
      final LocalDateTime endTime = LocalDateTime.now();
      log.info("Tried {} seconds. Expected event processed as {}", Duration.between(startTime, endTime).toSeconds(),
          processedModel);
      assertThat(processedModel).isNotNull();
    } catch (final Exception e) {
      log.info("Expected event not arrived in {} seconds", maxWaitInSec, e);
      fail("Expected event not arrived in " + maxWaitInSec + " seconds");
    }
  }

  private void sleepOneSecond() {
    try {
      TimeUnit.MILLISECONDS.sleep(1000);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Sleep interrupted", e);
    }
  }
}
