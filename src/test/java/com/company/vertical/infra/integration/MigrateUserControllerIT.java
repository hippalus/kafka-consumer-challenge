package com.company.vertical.infra.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;

import com.company.vertical.AbstractIT;
import com.company.vertical.IT;
import com.company.vertical.infra.common.ErrorResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@IT
class MigrateUserControllerIT extends AbstractIT {

  @Test
  void shouldAcceptedMigrationRequestByUserId() {
    //given:
    final var userId = 1234L;

    //when:
    final ResponseEntity<Void> responseEntity = this.testRestTemplate.exchange("/api/v1/migrate/user/" + userId, HttpMethod.POST,
        new HttpEntity<>(null, null), Void.class);

    // then: assert response
    assertThat(responseEntity).isNotNull()
        .returns(HttpStatus.ACCEPTED, from(ResponseEntity::getStatusCode))
        .returns(null, from(ResponseEntity::getBody));
  }

  @Test
  void shouldNotAcceptedMigrationRequestByInvalidUserId() {
    //given:
    final String userId = UUID.randomUUID().toString();

    //when:
    final ResponseEntity<ErrorResponse> responseEntity = this.testRestTemplate.exchange("/api/v1/migrate/user/" + userId,
        HttpMethod.POST,
        new HttpEntity<>(null, null), ErrorResponse.class);

    // then: assert response
    assertThat(responseEntity).isNotNull()
        .returns(HttpStatus.UNPROCESSABLE_ENTITY, from(ResponseEntity::getStatusCode));
  }

}