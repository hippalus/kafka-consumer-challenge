package com.company.vertical.domain.user.enrichment.model;

import lombok.Builder;

@Builder
public record PostComment(Long id, Long postId, String name, String email, String body) {

}
