package com.company.vertical.domain.user.enrichment.model;

public record PostComment(Long id, Long postId, String name, String email, String body) {

}
