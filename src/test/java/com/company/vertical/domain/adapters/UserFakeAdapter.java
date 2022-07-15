package com.company.vertical.domain.adapters;

import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserFakeAdapter implements UserPort {

  private final EnrichedUser enrichedUser;

  @Override
  public EnrichedUser enrichUser(final Long userId) {
    return this.enrichedUser;
  }
}
