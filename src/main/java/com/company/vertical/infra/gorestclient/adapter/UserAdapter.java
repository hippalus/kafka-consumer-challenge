package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class UserAdapter extends AbstractRestAdapter {

  private static final String USERS = "/users";
  private static final String POSTS = "/posts";
  private static final String TODOS = "/todos";

  public UserAdapter(final RequestOptions requestOptions, final WebClient webClient) {
    super(requestOptions, webClient);
  }

  public Flux<UserPostResponse> retrievePosts(final Long userId) {
    final String path = USERS + DELIMITER + Objects.requireNonNull(userId) + POSTS;

    return this.doGetFlux(path, UserPostResponse.class);
  }


  public Flux<UserTodoResponse> retrieveTodos(final Long userId) {
    final String path = USERS + DELIMITER + Objects.requireNonNull(userId) + TODOS;

    return this.doGetFlux(path, UserTodoResponse.class);
  }

}
