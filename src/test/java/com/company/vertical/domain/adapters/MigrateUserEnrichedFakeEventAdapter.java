package com.company.vertical.domain.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.enrichment.port.MigrateUserEnrichedEventPort;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrateUserEnrichedFakeEventAdapter implements MigrateUserEnrichedEventPort {

  private final List<MigrateUserEnriched> events = new ArrayList<>();

  @Override
  public void publish(final MigrateUserEnriched event) {
    this.events.add(event);
    log.info("[FAKE] MigrateUserEnriched is published: {}", event);

  }

  public void assertContains(final MigrateUserEnriched... events) {
    assertThat(events).containsAnyElementsOf(List.of(events));
  }

}
