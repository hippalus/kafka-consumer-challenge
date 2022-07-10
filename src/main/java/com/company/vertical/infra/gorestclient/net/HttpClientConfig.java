package com.company.vertical.infra.gorestclient.net;

import com.company.vertical.domain.common.exception.BusinessException;
import com.company.vertical.infra.gorestclient.exception.RemoteCallException;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

  private final RequestOptions requestOptions;

  @Bean
  public WebClient webClient(final WebClient.Builder builder) {
    return builder.baseUrl(this.requestOptions.getBaseUrl())
        .filter(this.errorHandler())
        .build();
  }

  @Bean
  public ExchangeFilterFunction errorHandler() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      if (clientResponse.statusCode().is5xxServerError()) {
        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> Mono.error(RemoteCallException.of(errorBody)));
      }
      if (clientResponse.statusCode().is4xxClientError()) {
        return clientResponse.bodyToMono(String.class).flatMap(errorBody -> Mono.error(BusinessException.of(errorBody)));
      }
      return Mono.just(clientResponse);
    });
  }

}
