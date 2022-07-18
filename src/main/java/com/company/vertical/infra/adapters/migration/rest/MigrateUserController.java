package com.company.vertical.infra.adapters.migration.rest;

import com.company.vertical.domain.common.usecase.BeanAwareUseCasePublisher;
import com.company.vertical.domain.user.migration.usecase.MigrateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/migrate/user")
public class MigrateUserController implements BeanAwareUseCasePublisher {

  @PostMapping("/{id}")
  public ResponseEntity<Void> migrateUserById(@PathVariable("id") final Long id) {
    this.publish(this.toUseCase(id));
    return ResponseEntity.accepted().build();
  }

  private MigrateUserUseCase toUseCase(final Long id) {
    return new MigrateUserUseCase(id);
  }
}
