package com.company.vertical.domain.user.enrichment.model;

import java.time.ZonedDateTime;

public record UserTodo(Long id, Long userId, String title, ZonedDateTime duesOn, String status) {

}
