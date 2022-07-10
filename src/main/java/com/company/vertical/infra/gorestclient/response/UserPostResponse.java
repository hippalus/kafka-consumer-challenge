package com.company.vertical.infra.gorestclient.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPostResponse(Long id, @JsonProperty("user_id") Long userId, String title, String body) {

}
