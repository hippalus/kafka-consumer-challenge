package com.company.vertical.infra.gorestclient;

import com.company.vertical.IT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@IT
class GoRestClientTest {

  @Autowired
  private GoRestClient goRestClient;

  @Test
  void shouldNotNullAdapters() {
    Assertions.assertNotNull(this.goRestClient.users());
    Assertions.assertNotNull(this.goRestClient.posts());
  }
}