package com.company.vertical.domain.user.enrichment.model;

import java.util.List;
import lombok.Builder;

@Builder
public record EnrichedUser(Long userId, List<UserTodo> userTodos, List<UserPost> userPosts,
                           List<PostComment> commentsOfEachPosts) {

}
