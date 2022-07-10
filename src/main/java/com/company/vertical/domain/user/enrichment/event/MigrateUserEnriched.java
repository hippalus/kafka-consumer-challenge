package com.company.vertical.domain.user.enrichment.event;

import com.company.vertical.domain.common.model.Event;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import java.util.List;

public record MigrateUserEnriched(Long userId, List<UserTodo> userTodos, List<UserPost> userPosts,
                                  List<PostComment> commentsOfEachPosts) implements Event {

  public static MigrateUserEnriched fromModel(final EnrichedUser enrichedUser) {
    return new MigrateUserEnriched(
        enrichedUser.userId(),
        enrichedUser.userTodos(),
        enrichedUser.userPosts(),
        enrichedUser.commentsOfEachPosts()
    );
  }

}
