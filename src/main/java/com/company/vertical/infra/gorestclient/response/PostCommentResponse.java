package com.company.vertical.infra.gorestclient.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PostCommentResponse(Long id, @JsonProperty("post_id") Long postId, String name, String email, String body) {

}
