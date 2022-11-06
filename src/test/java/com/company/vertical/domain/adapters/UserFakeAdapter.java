package com.company.vertical.domain.adapters;

import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.domain.user.enrichment.port.UserPort;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserFakeAdapter implements UserPort {

  private final EnrichedUser enrichedUser;

  @Override
  public List<UserPost> retrieveUserPosts(final Long userId) {
    return List.of(new UserPost(1L, userId, "User Post Title", "User Post Body"));
  }

  @Override
  public List<UserTodo> retrieveUserTodos(final Long userId) {
    return List.of(new UserTodo(1L, userId, "Todo Title", OffsetDateTime.parse("2022-07-18T00:00:00.000+05:30"), "completed"));
  }

  @Override
  public List<PostComment> retrievePostComments(final Long postId) {
    return List.of(new PostComment(1L, 1L, "Post Comment Name", "jhon_due@gmail.com", "Comment Body"));
  }
}
