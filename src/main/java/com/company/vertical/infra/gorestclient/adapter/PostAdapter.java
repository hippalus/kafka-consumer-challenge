package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class PostAdapter extends AbstractRestAdapter {

  private static final String POST_COMMENTS = "/posts/{postId}/comments";

  public PostAdapter(final RequestOptions requestOptions, final WebClient webClient) {
    super(requestOptions, webClient);
  }

  public Flux<PostCommentResponse> retrievePostComments(final Long postId) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(POST_COMMENTS)
        .buildAndExpand(Objects.requireNonNull(postId))
        .toUriString();

    return this.doGetFlux(uri, PostCommentResponse.class)
        .expand(response -> this.nextPage(response, PostCommentResponse.class))
        .flatMap(ResponseEntity::getBody)
        .filter(Objects::nonNull);
  }

}
