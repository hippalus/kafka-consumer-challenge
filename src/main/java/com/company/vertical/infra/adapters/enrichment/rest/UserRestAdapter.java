package com.company.vertical.infra.adapters.enrichment.rest;

import static com.company.vertical.infra.gorestclient.GoRestClient.X_PAGINATION_PAGES;

import com.company.vertical.domain.common.exception.BusinessException;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import com.company.vertical.infra.gorestclient.GoRestClient;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import com.company.vertical.infra.gorestclient.response.UserPostResponse;
import com.company.vertical.infra.gorestclient.response.UserTodoResponse;
import io.micrometer.core.instrument.util.StringUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRestAdapter implements UserPort {

  private static final Integer FIRST_PAGE = 1;
  private final GoRestClient goRestClient;

  @Override
  public List<UserPost> retrieveUserPosts(final Long userId) {
    final ResponseEntity<List<UserPostResponse>> responseEntity = this.goRestClient.users().retrievePosts(userId, FIRST_PAGE);
    final List<UserPostResponse> body = responseEntity.getBody();

    if (Objects.isNull(body)) {
      throw BusinessException.of("retrieveUserPosts null");
    }

    final Integer totalPages = getTotalPages(responseEntity);
    if (totalPages <= FIRST_PAGE) {
      return body.stream().map(toUserPostModel()).toList();
    }

    return IntStream.rangeClosed(2, totalPages).parallel()
        .mapToObj(page -> this.goRestClient.users().retrievePosts(userId, page))
        .<UserPostResponse>mapMulti((response, consumer) -> Objects.requireNonNull(response.getBody()).forEach(consumer))
        .map(toUserPostModel())
        .toList();
  }

  @Override
  public List<UserTodo> retrieveUserTodos(final Long userId) {
    final ResponseEntity<List<UserTodoResponse>> responseEntity = this.goRestClient.users().retrieveTodos(userId, FIRST_PAGE);
    final List<UserTodoResponse> body = responseEntity.getBody();

    if (Objects.isNull(body)) {
      throw BusinessException.of("retrieveUserTodos null");
    }

    final Integer totalPages = getTotalPages(responseEntity);
    if (totalPages <= FIRST_PAGE) {
      return body.stream().map(toUserTodoModel()).toList();
    }

    return IntStream.rangeClosed(2, totalPages).parallel()
        .mapToObj(page -> this.goRestClient.users().retrieveTodos(userId, page))
        .<UserTodoResponse>mapMulti((response, consumer) -> Objects.requireNonNull(response.getBody()).forEach(consumer))
        .map(toUserTodoModel())
        .toList();
  }

  @Override
  public List<PostComment> retrievePostComments(final Long postId) {
    final var responseEntity = this.goRestClient.posts().retrievePostComments(postId, FIRST_PAGE);
    final List<PostCommentResponse> body = responseEntity.getBody();

    if (Objects.isNull(body)) {
      throw BusinessException.of("retrievePostComments null");
    }

    final Integer totalPages = getTotalPages(responseEntity);
    if (totalPages <= FIRST_PAGE) {
      return body.stream().map(toPostCommentModel()).toList();
    }

    return IntStream.rangeClosed(2, totalPages).parallel()
        .mapToObj(page -> this.goRestClient.posts().retrievePostComments(postId, page))
        .<PostCommentResponse>mapMulti((response, consumer) -> Objects.requireNonNull(response.getBody()).forEach(consumer))
        .map(toPostCommentModel())
        .toList();
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

  private static Integer getTotalPages(final ResponseEntity<?> responseEntity) {
    final List<String> totalPages = responseEntity.getHeaders().get(X_PAGINATION_PAGES);
    if (CollectionUtils.isEmpty(totalPages) || StringUtils.isEmpty(totalPages.get(0))) {
      return FIRST_PAGE;
    }
    return Integer.valueOf(totalPages.get(0));
  }

}
