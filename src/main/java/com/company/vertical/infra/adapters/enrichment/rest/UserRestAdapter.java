package com.company.vertical.infra.adapters.enrichment.rest;

import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import com.company.vertical.infra.gorestclient.GoRestClient;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRestAdapter implements UserPort {

  private final GoRestClient goRestClient;

  @Override
  public EnrichedUser enrichUser(final Long userId) {
    final Flux<UserTodo> userTodos = this.retrieveUserTodos(userId);

    final Flux<UserPost> userPosts = this.retrieveUserPosts(userId);

    final Flux<PostComment> postComments = userPosts.map(UserPost::id).flatMap(this::retrievePostComments);

    return this.toEnrichedUserModel(userId, userTodos, userPosts, postComments).block();
  }

  public Flux<UserPost> retrieveUserPosts(final Long userId) {
    return this.goRestClient.users()
        .retrievePosts(userId)
        .map(toUserPostModel());
  }

  public Flux<UserTodo> retrieveUserTodos(final Long userId) {
    return this.goRestClient.users()
        .retrieveTodos(userId)
        .map(toUserTodoModel());
  }

  public Flux<PostComment> retrievePostComments(final Long postId) {
    return this.goRestClient.posts()
        .retrievePostComments(postId)
        .map(toPostCommentModel());
  }

  private Mono<EnrichedUser> toEnrichedUserModel(final Long userId, final Flux<UserTodo> userTodos,
      final Flux<UserPost> userPosts, final Flux<PostComment> postComments) {

    return Mono.zip(Mono.just(userId), userTodos.collectList(), userPosts.collectList(), postComments.collectList())
        .map(tuple4 -> new EnrichedUser(tuple4.getT1(), tuple4.getT2(), tuple4.getT3(), tuple4.getT4()));
  }


  private static Function<UserPostResponse, UserPost> toUserPostModel() {
    return response -> new UserPost(response.id(), response.userId(), response.title(), response.body());
  }

  private static Function<UserTodoResponse, UserTodo> toUserTodoModel() {
    return response -> new UserTodo(response.id(), response.userId(), response.title(), response.duesOn(), response.status());
  }

  private static Function<PostCommentResponse, PostComment> toPostCommentModel() {
    return item -> new PostComment(item.id(), item.postId(), item.name(), item.email(), item.body());
  }
}
