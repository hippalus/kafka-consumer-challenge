package com.company.vertical.infra.gorestclient.adapter;

import com.company.vertical.infra.gorestclient.request.RequestOptions;
import com.company.vertical.infra.gorestclient.response.PostCommentResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PostAdapter extends AbstractRestAdapter {

  private static final String POST_COMMENTS = "/posts/{postId}/comments";

  private final ParameterizedTypeReference<List<PostCommentResponse>> responseType = new ParameterizedTypeReference<>() {
  };

  public PostAdapter(final RequestOptions requestOptions, final RestTemplate restTemplate) {
    super(requestOptions, restTemplate);
  }

  public ResponseEntity<List<PostCommentResponse>> retrievePostComments(final Long postId, final Integer page) {
    final var uri = UriComponentsBuilder.fromHttpUrl(this.requestOptions.getBaseUrl())
        .path(POST_COMMENTS)
        .queryParam("page", page)
        .buildAndExpand(Objects.requireNonNull(postId))
        .toUriString();

    return this.doGet(uri, this.responseType);
  }

}
