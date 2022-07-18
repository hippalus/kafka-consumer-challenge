package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.auth.TokenProvider;
import com.company.vertical.infra.gorestclient.exception.RemoteCallException;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
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
public class UserAdapter extends AbstractRestAdapter {

  private static final String USERS = "/users";
  private static final String POSTS = "/posts";
  private static final String TODOS = "/todos";
  private static final String USER_ID = "/{userId}";
  private static final String USERS_POSTS = USERS + USER_ID + POSTS;
  private static final String USERS_TODOS = USERS + USER_ID + TODOS;


  private final ParameterizedTypeReference<List<UserPostResponse>> postResponseType = new ParameterizedTypeReference<>() {
  };
  private final ParameterizedTypeReference<List<UserTodoResponse>> todoResponseType = new ParameterizedTypeReference<>() {
  };

  public UserAdapter(final RestTemplate restTemplate, final RequestOptions requestOptions, final TokenProvider tokenProvider) {
    super(restTemplate, requestOptions, tokenProvider);
  }

  @Retryable(
      value = Exception.class,
      maxAttemptsExpression = "${gorest-client.max-attempts}",
      backoff = @Backoff(delayExpression = "${gorest-client.delay}")
  )
  public ResponseEntity<List<UserPostResponse>> retrievePosts(final Long userId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_POSTS)
        .queryParam(PAGE, Objects.requireNonNull(page))
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGet(uri, this.postResponseType);
  }

  @Recover
  public ResponseEntity<List<UserPostResponse>> retrievePosts(final Exception e, final Long postId, final Integer page) {
    log.error("Couldn't connect to GoRest api to do retrievePosts for post {} and page {} ", postId, page, e);
    throw RemoteCallException.of(e.getMessage());
  }


  @Retryable(
      value = Exception.class,
      maxAttemptsExpression = "${gorest-client.max-attempts}",
      backoff = @Backoff(delayExpression = "${gorest-client.delay}")
  )
  public ResponseEntity<List<UserTodoResponse>> retrieveTodos(final Long userId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_TODOS)
        .queryParam(PAGE, Objects.requireNonNull(page))
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGet(uri, this.todoResponseType);
  }

  @Recover
  public ResponseEntity<List<UserTodoResponse>> retrieveTodos(final Exception e, final Long postId, final Integer page) {
    log.error("Couldn't connect to GoRest api to do retrieveTodos for post {} and page {} ", postId, page, e);
    throw RemoteCallException.of(e.getMessage());
  }


}
