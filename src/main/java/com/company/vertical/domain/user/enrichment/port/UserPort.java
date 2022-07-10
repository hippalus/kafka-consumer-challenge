package com.company.vertical.domain.user.enrichment.port;

import com.company.vertical.domain.user.enrichment.model.EnrichedUser;

public interface UserPort {

  EnrichedUser enrichUser(Long userId);

}
