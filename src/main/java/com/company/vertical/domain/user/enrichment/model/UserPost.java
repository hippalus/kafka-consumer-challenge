package com.company.vertical.domain.user.enrichment.model;

import lombok.Builder;

@Builder
public record UserPost(Long id, Long userId, String title, String body) {

}
