package com.company.vertical.domain.user.migration;

import com.company.vertical.domain.common.usecase.ObservableUseCasePublisher;
import com.company.vertical.domain.common.usecase.VoidUseCaseHandler;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.company.vertical.domain.user.migration.usecase.MigrateUserUseCase;
import org.springframework.stereotype.Service;

@Service
public class MigrateUserUseCaseHandler implements ObservableUseCasePublisher, VoidUseCaseHandler<MigrateUserUseCase> {

  private final MigrateUserEventPort migrateUserEventPort;

  public MigrateUserUseCaseHandler(final MigrateUserEventPort migrateUserEventPort) {
    this.migrateUserEventPort = migrateUserEventPort;
    this.register(MigrateUserUseCase.class, this);
  }

  @Override
  public void handle(final MigrateUserUseCase useCase) {
    this.migrateUserEventPort.publish(new MigrateUser(useCase.userId()));
  }

}
