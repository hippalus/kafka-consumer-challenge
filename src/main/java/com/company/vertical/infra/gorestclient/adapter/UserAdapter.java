package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

  public UserAdapter(final RequestOptions requestOptions, final RestTemplate restTemplate) {
    super(requestOptions, restTemplate);
  }

  public ResponseEntity<List<UserPostResponse>> retrievePosts(final Long userId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_POSTS)
        .queryParam("page", Objects.requireNonNull(page))
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGet(uri, this.postResponseType);
  }


  public ResponseEntity<List<UserTodoResponse>> retrieveTodos(final Long userId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(USERS_TODOS)
        .queryParam("page", Objects.requireNonNull(page))
        .buildAndExpand(Objects.requireNonNull(userId))
        .toUriString();

    return this.doGet(uri, this.todoResponseType);
  }

}
