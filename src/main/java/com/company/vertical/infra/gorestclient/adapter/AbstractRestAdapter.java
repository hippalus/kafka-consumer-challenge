package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.exception.RemoteCallException;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
public abstract class AbstractRestAdapter {

  protected static final String DELIMITER = "/";
  private static final double JITTER_FACTOR = 0.75d;
  protected final RequestOptions requestOptions;
  protected final WebClient webClient;

  protected static Consumer<HttpHeaders> createHeaders(final RequestOptions options) {
    return httpHeaders -> {
      httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, options.getEncoding());
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      if (options.getAccessToken() != null) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + options.getAccessToken());
      }
    };
  }

  protected <T> Mono<T> doGetMono(final String path, final Class<T> clazz) {
    final String uri = this.requestOptions.getBaseUrl() + path;

    return this.webClient.get()
        .uri(uri)
        .headers(createHeaders(this.requestOptions))
        .retrieve()
        .bodyToMono(clazz);
  }

  protected <T> Flux<T> doGetFlux(final String path, final Class<T> clazz) {
    final String uri = this.requestOptions.getBaseUrl() + path;

    return this.webClient.get()
        .uri(uri)
        .headers(createHeaders(this.requestOptions))
        .retrieve()
        .bodyToFlux(clazz)
        .retryWhen(
            Retry.backoff(5, Duration.ofSeconds(5))
                .jitter(JITTER_FACTOR)
                .filter(RemoteCallException.class::isInstance)
        );
  }
}
