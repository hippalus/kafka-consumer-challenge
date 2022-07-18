package com.company.vertical.infra.common.usecase;

import com.company.vertical.domain.common.usecase.ObservableUseCasePublisher;
import com.company.vertical.domain.common.usecase.UseCaseHandler;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Getter
@Service
@Primary
@ConditionalOnProperty(name = "usecase.enabled", havingValue = "false", matchIfMissing = true)
public class FakeEnrichUserUseCaseHandler implements ObservableUseCasePublisher, UseCaseHandler<EnrichedUser, EnrichUser> {

  EnrichedUser enrichedUser;

  public FakeEnrichUserUseCaseHandler() {
    this.register(EnrichUser.class, this);
  }

  @Override
  public EnrichedUser handle(final EnrichUser useCase) {
    this.enrichedUser = EnrichedUser.builder()
        .userId(useCase.userId())
        .build();

    return this.enrichedUser;
  }
}
