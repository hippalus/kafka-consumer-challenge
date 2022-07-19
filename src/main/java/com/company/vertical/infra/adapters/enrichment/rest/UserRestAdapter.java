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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
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
  private static final Integer SECOND_PAGE = 2;
  private final GoRestClient goRestClient;

  @Override
  public List<UserPost> retrieveUserPosts(final Long userId) {
    return callApi(userId, toUserPostModel(), (req, page) -> this.goRestClient.users().retrievePosts(req, page));
  }

  @Override
  public List<UserTodo> retrieveUserTodos(final Long userId) {
    return callApi(userId, toUserTodoModel(), (req, page) -> this.goRestClient.users().retrieveTodos(req, page));
  }

  @Override
  public List<PostComment> retrievePostComments(final Long postId) {
    return callApi(postId, toPostCommentModel(), (req, page) -> this.goRestClient.posts().retrievePostComments(req, page));
  }

  private static <M, R, T> List<M> callApi(final T request, final Function<? super R, ? extends M> responseToModelMapper,
      final BiFunction<T, ? super Integer, ? extends ResponseEntity<List<R>>> apiCall) {

    final ResponseEntity<List<R>> responseEntity = apiCall.apply(request, FIRST_PAGE);

    final List<R> body = responseEntity.getBody();
    if (Objects.isNull(body)) {
      throw BusinessException.of("Response body is null!");
    }

    final Stream<M> firstPage = body.stream().map(responseToModelMapper);

    final Integer totalPages = getTotalPages(responseEntity);
    if (totalPages <= FIRST_PAGE) {
      return firstPage.toList();
    }

    final Stream<M> otherPages = IntStream.rangeClosed(SECOND_PAGE, totalPages).parallel()
        .mapToObj(page -> apiCall.apply(request, page))
        .<R>mapMulti((response, consumer) -> Objects.requireNonNull(response.getBody()).forEach(consumer))
        .map(responseToModelMapper);

    return Stream.concat(firstPage, otherPages).toList();
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
