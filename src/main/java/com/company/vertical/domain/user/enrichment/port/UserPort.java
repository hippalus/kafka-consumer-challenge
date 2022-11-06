package com.company.vertical.domain.user.enrichment.port;

import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import java.util.List;

public interface UserPort {

  List<UserPost> retrieveUserPosts(Long userId);

  List<UserTodo> retrieveUserTodos(Long userId);

  List<PostComment> retrievePostComments(Long postId);

}
