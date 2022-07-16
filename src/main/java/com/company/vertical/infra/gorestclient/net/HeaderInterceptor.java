package com.company.vertical.infra.gorestclient.net;

import com.company.vertical.infra.gorestclient.auth.TokenProvider;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeaderInterceptor implements ClientHttpRequestInterceptor {

  private final TokenProvider tokenProvider;
  private final RequestOptions requestOptions;

  @Override
  @SneakyThrows
  public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) {
    request.getHeaders().addAll(this.defaultHeaders());

    final ClientHttpResponse response = execution.execute(request, body);
    final HttpStatus responseStatus = response.getStatusCode();

    if (responseStatus == HttpStatus.TOO_MANY_REQUESTS || responseStatus == HttpStatus.UNAUTHORIZED) {
      return execution.execute(this.refreshToken(request), body);
    }

    return response;
  }

  private HttpHeaders defaultHeaders() {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, this.requestOptions.getEncoding());
    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenProvider.current());
    return httpHeaders;
  }

  private HttpRequest refreshToken(final HttpRequest request) {
    request.getHeaders().replace(HttpHeaders.AUTHORIZATION, List.of("Bearer " + this.tokenProvider.next()));
    return request;
  }

}
