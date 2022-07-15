package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import io.micrometer.core.instrument.util.StringUtils;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@RequiredArgsConstructor
public abstract class AbstractRestAdapter {

  protected static final double JITTER_FACTOR = 0.75d;
  protected static final String X_LINKS_NEXT = "X-Links-Next";
  protected static final int MAX_ATTEMPTS = 5;
  protected static final int MIN_BACK_OF_SECOND = 5;

  protected final RequestOptions requestOptions;
  protected final WebClient webClient;

  protected <T> Mono<ResponseEntity<Flux<T>>> doGetFlux(final String uri, final Class<T> clazz) {
    return this.webClient.get()
        .uri(uri)
        .retrieve()
        .toEntityFlux(clazz)
        .retryWhen(retryBackOffSpec());
  }

  protected <T> Mono<ResponseEntity<Flux<T>>> nextPage(final ResponseEntity<Flux<T>> response, final Class<T> clazz) {
    final List<String> next = response.getHeaders().get(X_LINKS_NEXT);
    if (CollectionUtils.isEmpty(next) || StringUtils.isEmpty(next.get(0))) {
      return Mono.empty();
    }
    return this.doGetFlux(next.get(0), clazz);
  }


  protected static RetryBackoffSpec retryBackOffSpec() {
    return Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(MIN_BACK_OF_SECOND)).jitter(JITTER_FACTOR);
  }

}
