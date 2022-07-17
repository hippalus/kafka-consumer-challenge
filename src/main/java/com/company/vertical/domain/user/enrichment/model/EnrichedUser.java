package com.company.vertical.domain.user.enrichment.model;

import java.util.List;
import lombok.Builder;
import org.springframework.util.CollectionUtils;

@Builder
public record EnrichedUser(Long userId, List<UserTodo> userTodos, List<UserPost> userPosts,
                           List<PostComment> commentsOfEachPosts) {

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(this.userTodos) && CollectionUtils.isEmpty(this.userPosts) &&
        CollectionUtils.isEmpty(this.commentsOfEachPosts);
  }
}
