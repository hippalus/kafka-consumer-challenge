package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class PostAdapter extends AbstractRestAdapter {

  private static final String POSTS = "/posts";
  private static final String COMMENTS = "/comments";

  public PostAdapter(final RequestOptions requestOptions, final WebClient webClient) {
    super(requestOptions, webClient);
  }

  public Flux<PostCommentResponse> retrievePostComments(final Long postId) {
    final String path = POSTS + DELIMITER + Objects.requireNonNull(postId) + COMMENTS;

    return this.doGetFlux(path, PostCommentResponse.class);
  }
}
