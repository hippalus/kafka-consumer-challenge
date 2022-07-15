package com.company.vertical.infra.gorestclient.net;

import com.company.vertical.domain.common.exception.BusinessException;
import com.company.vertical.infra.gorestclient.auth.TokenProvider;
import com.company.vertical.infra.gorestclient.exception.RemoteCallException;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

  private final RequestOptions requestOptions;
  private final TokenProvider tokenProvider;

  @Bean
  public WebClient webClient(final WebClient.Builder builder) {
    return builder.baseUrl(this.requestOptions.getBaseUrl())
        .defaultHeaders(this.defaultHeaders())
        .filter(this.errorHandler())
        .filter(this.retryOn429Or401())
        .build();
  }

  public Consumer<HttpHeaders> defaultHeaders() {
    return httpHeaders -> {
      httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, this.requestOptions.getEncoding());
      httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenProvider.current());
    };
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

  @Bean
  public ExchangeFilterFunction retryOn429Or401() {
    return (request, next) -> next.exchange(request)
        .flatMap((Function<ClientResponse, Mono<ClientResponse>>) clientResponse -> {
          final HttpStatus responseStatus = clientResponse.statusCode();
          if (responseStatus == HttpStatus.TOO_MANY_REQUESTS || responseStatus == HttpStatus.UNAUTHORIZED) {
            return next.exchange(this.refreshToken(request));
          }
          return Mono.just(clientResponse);
        });
  }

  private ClientRequest refreshToken(final ClientRequest request) {
    return ClientRequest.from(request)
        .headers(httpHeaders -> httpHeaders.replace(HttpHeaders.AUTHORIZATION, List.of("Bearer " + this.tokenProvider.next())))
        .build();
  }
}
