package com.company.vertical.domain.user.enrichment;

import com.company.vertical.domain.common.usecase.ObservableUseCasePublisher;
import com.company.vertical.domain.common.usecase.UseCaseHandler;
import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.domain.user.enrichment.port.MigrateUserEnrichedEventPort;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "usecase.enabled", havingValue = "true")
public class EnrichUserUseCaseHandler implements ObservableUseCasePublisher, UseCaseHandler<EnrichedUser, EnrichUser> {

  private final UserPort userPort;
  private final MigrateUserEnrichedEventPort migrateUserEnrichedEventPort;

  public EnrichUserUseCaseHandler(final UserPort userPort, final MigrateUserEnrichedEventPort migrateUserEnrichedEventPort) {
    this.userPort = userPort;
    this.migrateUserEnrichedEventPort = migrateUserEnrichedEventPort;
    this.register(EnrichUser.class, this);
  }

  @Override
  public EnrichedUser handle(final EnrichUser useCase) {
    final Long userId = useCase.userId();

    final CompletableFuture<List<UserTodo>> userTodosFuture = this.getAsyncUserTodos(userId);
    final CompletableFuture<List<UserPost>> userPostsFuture = this.getAsyncUserPosts(userId);
    final CompletableFuture<List<PostComment>> postCommentsFuture = userPostsFuture.thenCompose(this::getAsyncPostComments);

    final EnrichedUser enrichedUser = this.combine(userId, userTodosFuture, userPostsFuture, postCommentsFuture);

    if (!enrichedUser.isEmpty()) {
      this.migrateUserEnrichedEventPort.publish(MigrateUserEnriched.fromModel(enrichedUser));
      log.info("User enrichment completed successfully. {}", enrichedUser);
    }

    return enrichedUser;
  }

  private EnrichedUser combine(final Long userId,
      final CompletableFuture<? extends List<UserTodo>> userTodosFuture,
      final CompletableFuture<? extends List<UserPost>> userPostsFuture,
      final CompletableFuture<? extends List<PostComment>> postCommentsFuture) {

    return CompletableFuture.allOf(userTodosFuture, userPostsFuture, postCommentsFuture)
        .thenApply(ignoredVoid -> EnrichedUser.builder()
            .userId(userId)
            .userTodos(userTodosFuture.join())
            .userPosts(userPostsFuture.join())
            .commentsOfEachPosts(postCommentsFuture.join())
            .build())
        .join();
  }

  private CompletableFuture<List<UserTodo>> getAsyncUserTodos(final Long userId) {
    return CompletableFuture.supplyAsync(() -> this.userPort.retrieveUserTodos(userId));
  }

  private CompletableFuture<List<UserPost>> getAsyncUserPosts(final Long userId) {
    return CompletableFuture.supplyAsync(() -> this.userPort.retrieveUserPosts(userId));
  }

  private CompletableFuture<List<PostComment>> getAsyncPostComments(final List<UserPost> userPosts) {
    return CompletableFuture.supplyAsync(() -> this.retrieveEachPostComments(userPosts));
  }

  private List<PostComment> retrieveEachPostComments(final List<UserPost> userPosts) {
    return userPosts.stream().parallel()
        .map(UserPost::id)
        .<PostComment>mapMulti((postId, consumer) -> this.userPort.retrievePostComments(postId).forEach(consumer))
        .toList();
  }

}
