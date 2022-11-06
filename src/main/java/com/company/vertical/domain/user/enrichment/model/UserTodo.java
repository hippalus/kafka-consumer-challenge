package com.company.vertical.domain.user.enrichment.model;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record UserTodo(Long id, Long userId, String title, OffsetDateTime duesOn, String status) {

}
