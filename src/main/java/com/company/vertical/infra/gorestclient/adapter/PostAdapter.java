package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.auth.TokenProvider;
import com.company.vertical.infra.gorestclient.exception.RemoteCallException;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class PostAdapter extends AbstractRestAdapter {

  private static final String POST_COMMENTS = "/posts/{postId}/comments";

  private final ParameterizedTypeReference<List<PostCommentResponse>> responseType = new ParameterizedTypeReference<>() {
  };

  public PostAdapter(final RestTemplate restTemplate, final RequestOptions requestOptions, final TokenProvider tokenProvider) {
    super(restTemplate, requestOptions, tokenProvider);
  }

  @Retryable(
      value = Exception.class,
      maxAttempts = 5,
      backoff = @Backoff(delayExpression = "2000")
  )
  public ResponseEntity<List<PostCommentResponse>> retrievePostComments(final Long postId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(POST_COMMENTS)
        .queryParam("page", page)
        .buildAndExpand(Objects.requireNonNull(postId))
        .toUriString();

    return this.doGet(uri, this.responseType);
  }

  @Recover
  public ResponseEntity<List<PostCommentResponse>> retrievePostComments(final Exception e, final Long postId,
      final Integer page) {
    log.error("Couldn't connect to GoRest api to do retrievePostComments for post {} and page {} ", postId, page, e);
    throw RemoteCallException.of(e.getMessage());
  }


}
