package com.company.vertical.domain.user.enrichment;

import com.company.vertical.domain.common.usecase.ObservableUseCasePublisher;
import com.company.vertical.domain.common.usecase.UseCaseHandler;
import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.port.MigrateUserEnrichedEventPort;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EnrichUserUseCaseHandler implements ObservableUseCasePublisher, UseCaseHandler<EnrichedUser, EnrichUser> {

  private final UserPort userPort;
  private final MigrateUserEnrichedEventPort migrateUserEnrichedEventPort;

  public EnrichUserUseCaseHandler(final UserPort userPort, final MigrateUserEnrichedEventPort migrateUserEnrichedEventPort) {
    this.userPort = userPort;
    this.migrateUserEnrichedEventPort = migrateUserEnrichedEventPort;
    this.register(EnrichUser.class, this);
  }

  @Override
  public EnrichedUser handle(final EnrichUser useCase) {
    final EnrichedUser enrichedUser = this.userPort.enrichUser(useCase.userId());
    this.migrateUserEnrichedEventPort.publish(MigrateUserEnriched.fromModel(enrichedUser));
    log.info("User enrichment completed successfully. {}", enrichedUser);
    return enrichedUser;
  }

}
