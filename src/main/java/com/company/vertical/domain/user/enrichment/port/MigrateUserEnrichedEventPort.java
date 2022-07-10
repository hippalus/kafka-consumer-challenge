package com.company.vertical.domain.user.enrichment.port;

import com.company.vertical.domain.common.event.EventPublisher;
import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;

public interface MigrateUserEnrichedEventPort extends EventPublisher<MigrateUserEnriched> {

  @Override
  void publish(final MigrateUserEnriched event);

}

