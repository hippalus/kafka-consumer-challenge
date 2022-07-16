package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public abstract class AbstractRestAdapter {

  protected static final double JITTER_FACTOR = 0.75d;
  protected static final String X_LINKS_NEXT = "X-Links-Next";
  protected static final int MAX_ATTEMPTS = 5;
  protected static final int MIN_BACK_OF_SECOND = 5;

  protected final RequestOptions requestOptions;
  protected final RestTemplate restTemplate;

  protected <T> ResponseEntity<T> doGet(final String uri, final ParameterizedTypeReference<T> typeReference) {
    return this.restTemplate.exchange(uri, HttpMethod.GET, null, typeReference);
  }

}
