package com.company.vertical.infra.gorestclient.auth;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenProvider implements TokenProvider {

  private final Queue<String> tokens;

  public InMemoryTokenProvider(final RequestOptions requestOptions) {
    this.tokens = new LinkedBlockingDeque<>(List.of(Objects.requireNonNull(requestOptions.getTokens())));
  }

  @Override
  public String current() {
    return this.tokens.peek();
  }

  @Override
  public String next() {
    this.tokens.add(this.tokens.remove());
    return this.current();
  }

  @Override
  public List<String> tokens() {
    return this.tokens.stream().toList();
  }

}
