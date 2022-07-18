package com.company.vertical.infra.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.assertj.core.api.Assertions.assertThat;

import com.company.vertical.AbstractIT;
import com.company.vertical.IT;
import com.company.vertical.domain.user.enrichment.model.PostComment;
import com.company.vertical.domain.user.enrichment.model.UserPost;
import com.company.vertical.domain.user.enrichment.model.UserTodo;
import com.company.vertical.infra.adapters.enrichment.rest.UserRestAdapter;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@IT
class UserRestAdapterIT extends AbstractIT {

  private static final WireMockServer wireMockServer = new WireMockServer(9994);

  @Autowired
  UserRestAdapter userRestAdapter;

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {

    wireMockServer.start();

    wireMockServer.baseUrl();

    wireMockServer.stubFor(
        get(urlMatching("/users/1234/posts\\??(?:&?[^=&]*=[^=&]*)*"))
            .willReturn(aResponse()
                .withBodyFile("user_posts.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/users/1234/todos\\??(?:&?[^=&]*=[^=&]*)*"))
            .willReturn(aResponse()
                .withBodyFile("todos.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/posts/1123/comments\\??(?:&?[^=&]*=[^=&]*)*"))
            .willReturn(aResponse()
                .withBodyFile("comments.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/users/1234/posts"))
            .withQueryParam("page", equalTo("3"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.TOO_MANY_REQUESTS.value())
            )
    );
  }

  @AfterAll
  static void afterAll() {
    wireMockServer.stop();
  }

  public static void main(final String[] args) {
    final OffsetDateTime parse = OffsetDateTime.parse("2022-08-07T00:00:00.000+05:30", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    System.out.println(parse.toInstant());
  }

  @Test
  void shouldGetUserPosts() {
    //given:
    final Long userId = 1234L;

    final UserPost expected = UserPost.builder()
        .id(1123L)
        .userId(userId)
        .title("Bis conspergo vestrum sonitus abscido consequuntur confugo audentia.")
        .body("Id theca curriculum. Cumque capitulus denego. Cunctatio curvus consequuntur. Aufero iusto agnitio."
            + " Comprehendo versus cupio. Adultus quo apud. Talio paens usitas. Uberrime theatrum tredecim."
            + " Cervus deleniti arx. Caput qui sollers. Videlicet uxor baiulus. Allatus bestia spero. "
            + "Concedo quia quae. Crinis sub qui. A ut carcer. Vergo spargo tribuo. Acies est nostrum."
            + " Textilis distinctio consequatur. Animi custodia cunctatio. Vilicus depereo conforto.")
        .build();

    //when:
    final List<UserPost> userPosts = this.userRestAdapter.retrieveUserPosts(userId);

    //then:
    assertThat(userPosts).isNotNull().isNotEmpty().hasSize(1);
    assertThat(userPosts.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldGetUserTodos() {
    //given:
    final Long userId = 1234L;

    final UserTodo expected = UserTodo.builder()
        .id(1315L)
        .userId(userId)
        .status("completed")
        .duesOn(OffsetDateTime.parse("2022-08-06T18:30Z"))
        .title("Ocer tepesco cauda reiciendis ullam ancilla venia aggredior bonus accusator pauci.")
        .build();

    //when:
    final List<UserTodo> userPosts = this.userRestAdapter.retrieveUserTodos(userId);

    //then:
    assertThat(userPosts).isNotNull().isNotEmpty().hasSize(1);
    assertThat(userPosts.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldRetrieveCommentsOfPost() {
    //given:
    final Long postId = 1123L;

    final PostComment first = PostComment.builder()
        .id(1125L)
        .postId(postId)
        .name("Rep. Anand Iyengar")
        .email("anand_rep_iyengar@reichert.io")
        .body("Eos nihil inventore. Tenetur in distinctio.")
        .build();

    final PostComment second = PostComment.builder()
        .id(1124L)
        .postId(postId)
        .name("Dhara Chopra")
        .email("chopra_dhara@lesch.com")
        .body("Temporibus qui aut. Quisquam sit perferendis. Et impedit qui. Quaerat possimus ut.")
        .build();

    final List<PostComment> expected = List.of(first, second);

    //when:
    final List<PostComment> userPosts = this.userRestAdapter.retrievePostComments(postId);

    //then:
    assertThat(userPosts)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .isEqualTo(expected);
  }
}