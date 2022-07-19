package com.company.vertical.infra.gorestclient;

import com.company.vertical.infra.gorestclient.auth.InMemoryTokenProvider;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTokenProviderTest {


  InMemoryTokenProvider inMemoryTokenProvider;

  @BeforeEach
  void setUp() {
    final RequestOptions requestOptions = new RequestOptions();
    requestOptions.setTokens(new String[]{"Habip", "Hakan", "Isler"});
    this.inMemoryTokenProvider = new InMemoryTokenProvider(requestOptions);
  }

  @Test
  void tokenCircleTest() {
    final List<String> tokens = this.inMemoryTokenProvider.tokens();
    Assertions.assertThat(tokens).isEqualTo(List.of("Habip", "Hakan", "Isler"));

    final String current = this.inMemoryTokenProvider.current();
    Assertions.assertThat(current).isEqualTo("Habip");

    final String next = this.inMemoryTokenProvider.next();
    Assertions.assertThat(next).isEqualTo("Hakan");

    final List<String> newTokenOrder = this.inMemoryTokenProvider.tokens();
    Assertions.assertThat(newTokenOrder).isEqualTo(List.of("Hakan", "Isler", "Habip"));

    final String nextNext = this.inMemoryTokenProvider.next();
    Assertions.assertThat(nextNext).isEqualTo("Isler");

    final List<String> newTokenOrder2 = this.inMemoryTokenProvider.tokens();
    Assertions.assertThat(newTokenOrder2).isEqualTo(List.of("Isler", "Habip", "Hakan"));

    final String nextNextNext = this.inMemoryTokenProvider.next();
    Assertions.assertThat(nextNextNext).isEqualTo("Habip");

    final List<String> tokensInitialOrder = this.inMemoryTokenProvider.tokens();
    Assertions.assertThat(tokensInitialOrder).isEqualTo(List.of("Habip", "Hakan", "Isler"));
  }
}
