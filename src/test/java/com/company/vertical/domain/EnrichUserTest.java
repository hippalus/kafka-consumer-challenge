package com.company.vertical.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.company.vertical.domain.adapters.MigrateUserEnrichedFakeEventAdapter;
import com.company.vertical.domain.adapters.UserFakeAdapter;
import com.company.vertical.domain.user.enrichment.EnrichUserUseCaseHandler;
import com.company.vertical.domain.user.enrichment.event.MigrateUserEnriched;
import com.company.vertical.domain.user.enrichment.model.EnrichedUser;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.domain.user.enrichment.usecase.EnrichUser;
import java.time.OffsetDateTime;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnrichUserTest {

  private EnrichUserUseCaseHandler enrichUserUseCaseHandler;
  private final UserFakeAdapter userFakeAdapter = new UserFakeAdapter(this.createEnrichedUser(1L));
  private final MigrateUserEnrichedFakeEventAdapter migrateUserEnrichedFakeEventAdapter = new MigrateUserEnrichedFakeEventAdapter();

  @BeforeEach
  void setUp() {
    this.enrichUserUseCaseHandler = new EnrichUserUseCaseHandler(this.userFakeAdapter, this.migrateUserEnrichedFakeEventAdapter);
  }

  @Test
  void shouldEnrichUserAndPublishMigrateUserEnrichedEvent() {
    // given
    final EnrichUser useCase = new EnrichUser(1L);

    //when:
    final EnrichedUser enrichedUser = this.enrichUserUseCaseHandler.handle(useCase);

    //then:
    assertThat(enrichedUser).isEqualTo(this.createEnrichedUser(1L));
    this.migrateUserEnrichedFakeEventAdapter.assertContains(MigrateUserEnriched.fromModel(enrichedUser));
  }


  @NotNull
  private EnrichedUser createEnrichedUser(final Long userId) {
    return new EnrichedUser(
        1L,
        List.of(new UserTodo(1L, userId, "Todo Title", OffsetDateTime.parse("2022-07-18T00:00:00.000+05:30"), "completed")),
        List.of(new UserPost(1L, userId, "User Post Title", "User Post Body")),
        List.of(new PostComment(1L, 1L, "Post Comment Name", "jhon_due@gmail.com", "Comment Body"))
    );
  }

}
