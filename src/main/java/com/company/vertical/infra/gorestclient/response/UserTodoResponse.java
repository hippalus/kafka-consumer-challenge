package com.company.vertical.infra.gorestclient.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public record UserTodoResponse(
    Long id,
    @JsonProperty("user_id")
    Long userId,
    String title,
    @JsonProperty("due_on")
    ZonedDateTime duesOn,
    String status) {

}
