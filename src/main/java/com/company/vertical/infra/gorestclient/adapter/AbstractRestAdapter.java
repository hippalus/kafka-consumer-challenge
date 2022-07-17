package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.auth.TokenProvider;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRestAdapter {

  protected final RestTemplate restTemplate;
  protected final RequestOptions requestOptions;
  protected final TokenProvider tokenProvider;

  public <T> ResponseEntity<T> doGet(final String uri, final ParameterizedTypeReference<T> type) {
    try {
      return this.restTemplate.exchange(uri, HttpMethod.GET, this.createHttpEntity(), type);
    } catch (final HttpClientErrorException ex) {
      final HttpStatus statusCode = ex.getStatusCode();
      if (statusCode == HttpStatus.TOO_MANY_REQUESTS || statusCode == HttpStatus.UNAUTHORIZED) {
        log.warn("Starting retry request for {} with a new token.", uri);
        return this.restTemplate.exchange(uri, HttpMethod.GET, this.createHttpEntityWithRefreshedToken(), type);
      }
      throw ex;
    }
  }

  protected <T> HttpEntity<T> createHttpEntity() {
    return new HttpEntity<>(this.createHeaders());
  }

  protected <T> HttpEntity<T> createHttpEntityWithRefreshedToken() {
    return new HttpEntity<>(this.refreshAuthToken());
  }

  protected HttpHeaders createHeaders() {
    final HttpHeaders headers = defaultHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenProvider.current());
    return headers;
  }

  protected HttpHeaders refreshAuthToken() {
    final HttpHeaders headers = defaultHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenProvider.next());
    return headers;
  }

  private static HttpHeaders defaultHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCEPT_ENCODING, "gzip");
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

}
