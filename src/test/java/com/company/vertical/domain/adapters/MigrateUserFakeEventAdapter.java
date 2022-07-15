package com.company.vertical.domain.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.migration.MigrateUserEventPort;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MigrateUserFakeEventAdapter implements MigrateUserEventPort {

  private final List<MigrateUser> events = new ArrayList<>();

  @Override
  public void publish(final MigrateUser event) {
    this.events.add(event);
    log.info("[FAKE] MigrateUser is published: {}", event);
  }

  public void assertContains(final MigrateUser... events) {
    assertThat(events).containsAnyElementsOf(List.of(events));
  }
}
