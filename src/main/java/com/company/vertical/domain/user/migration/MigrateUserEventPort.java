package com.company.vertical.domain.user.migration;

import com.company.vertical.domain.common.event.EventPublisher;
import com.company.vertical.domain.user.migration.event.MigrateUser;

public interface MigrateUserEventPort extends EventPublisher<MigrateUser> {

  @Override
  void publish(final MigrateUser event);
}