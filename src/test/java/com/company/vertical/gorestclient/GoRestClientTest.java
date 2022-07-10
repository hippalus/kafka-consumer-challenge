package com.company.vertical.gorestclient;

import com.company.vertical.infra.gorestclient.GoRestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoRestClientTest {

  @Autowired
  private GoRestClient goRestClient;

  @Test
  void shouldNotNullAdapters() {
    Assertions.assertNotNull(this.goRestClient.users());
    Assertions.assertNotNull(this.goRestClient.posts());
  }
}