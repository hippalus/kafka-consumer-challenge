package com.company.vertical.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.company.vertical.domain.adapters.MigrateUserFakeEventAdapter;
import com.company.vertical.domain.user.migration.MigrateUserUseCaseHandler;
import com.company.vertical.domain.user.migration.event.MigrateUser;
import com.company.vertical.domain.user.migration.usecase.MigrateUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MigrateUserTest {

  private MigrateUserUseCaseHandler migrateUserUseCaseHandler;
  private final MigrateUserFakeEventAdapter migrateUserFakeEventAdapter = new MigrateUserFakeEventAdapter();

  @BeforeEach
  void setUp() {
    this.migrateUserUseCaseHandler = new MigrateUserUseCaseHandler(this.migrateUserFakeEventAdapter);
  }

  @Test
  void shouldPublishMigrateUserEvent() {
    //given:
    final MigrateUserUseCase migrateUserUseCase = new MigrateUserUseCase(1L);

    //when:
    this.migrateUserUseCaseHandler.handle(migrateUserUseCase);

    //then:
    this.migrateUserFakeEventAdapter.assertContains(new MigrateUser(1L));
  }

  @Test
  void shouldNotPublishMigrateUserEventWhenUserIdNull() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new MigrateUserUseCase(null))
        .withMessage("userId is marked non-null but is null");
  }
}
