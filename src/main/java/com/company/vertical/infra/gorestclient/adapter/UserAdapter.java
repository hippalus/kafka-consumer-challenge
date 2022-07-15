package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class UserAdapter extends AbstractRestAdapter {

  private static final String USERS = "/users";
  private static final String POSTS = "/posts";
  private static final String TODOS = "/todos";
  private static final String USER_ID = "/{userId}";
  private static final String USERS_POSTS = USERS + USER_ID + POSTS;
  private static final String USERS_TODOS = USERS + USER_ID + TODOS;

  public UserAdapter(final RequestOptions requestOptions, final WebClient webClient) {
    super(requestOptions, webClient);
  }

  public Flux<UserPostResponse> retrievePosts(final Long userId) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_POSTS)
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGetFlux(uri, UserPostResponse.class)
        .expand(response -> this.nextPage(response, UserPostResponse.class))
        .flatMap(ResponseEntity::getBody)
        .filter(Objects::nonNull);
  }


  public Flux<UserTodoResponse> retrieveTodos(final Long userId) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_TODOS)
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGetFlux(uri, UserTodoResponse.class)
        .expand(response -> this.nextPage(response, UserTodoResponse.class))
        .flatMap(ResponseEntity::getBody)
        .filter(Objects::nonNull);
  }

}
