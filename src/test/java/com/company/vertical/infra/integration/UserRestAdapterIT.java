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
import com.company.vertical.infra.gorestclient.GoRestClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.OffsetDateTime;
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

    wireMockServer.stubFor(
        get(urlMatching("/users/1234/posts\\??(?:&?[^=&]*=[^=&]*)*"))
            .willReturn(aResponse()
                .withBodyFile("user_posts.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/users/8976/posts\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("1"))
            .willReturn(aResponse()
                .withBodyFile("user_posts_page1.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/users/8976/posts\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("2"))
            .willReturn(aResponse()
                .withBodyFile("user_posts_page2.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
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
        get(urlMatching("/users/2345/todos\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("1"))
            .willReturn(aResponse()
                .withBodyFile("todos_page1.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/users/2345/todos\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("2"))
            .willReturn(aResponse()
                .withBodyFile("todos_page2.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
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
        get(urlMatching("/posts/5678/comments\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("1"))
            .willReturn(aResponse()
                .withBodyFile("comments_page1.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
                .withStatus(HttpStatus.OK.value())
            )
    );

    wireMockServer.stubFor(
        get(urlMatching("/posts/5678/comments\\??(?:&?[^=&]*=[^=&]*)*"))
            .withQueryParam("page", equalTo("2"))
            .willReturn(aResponse()
                .withBodyFile("comments_page2.json")
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(GoRestClient.X_PAGINATION_PAGES, "2")
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

  @Test
  void shouldRetrieveUserPosts() {
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
  void shouldRetrieveAllUserPostsPages() {
    //given:
    final Long userId = 8976L;

    final UserPost first = UserPost.builder()
        .id(1129L)
        .userId(userId)
        .title("Title1")
        .body("Body1")
        .build();

    final UserPost second = UserPost.builder()
        .id(1130L)
        .userId(userId)
        .title("Title2")
        .body("Body2")
        .build();

    final List<UserPost> expected = List.of(first, second);

    //when:
    final List<UserPost> userPosts = this.userRestAdapter.retrieveUserPosts(userId);

    //then:
    assertThat(userPosts)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .isEqualTo(expected);
  }


  @Test
  void shouldRetrieveUserTodos() {
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
    final List<UserTodo> userTodos = this.userRestAdapter.retrieveUserTodos(userId);

    //then:
    assertThat(userTodos).isNotNull().isNotEmpty().hasSize(1);
    assertThat(userTodos.get(0)).isEqualTo(expected);
  }

  @Test
  void shouldRetrieveAllTodosPages() {
    //given:
    final Long userId = 2345L;

    final UserTodo first = UserTodo.builder()
        .id(1316L)
        .userId(userId)
        .status("completed")
        .duesOn(OffsetDateTime.parse("2022-08-07T06:30Z"))
        .title("Title")
        .build();

    final UserTodo second = UserTodo.builder()
        .id(1317L)
        .userId(userId)
        .status("completed")
        .duesOn(OffsetDateTime.parse("2022-08-07T07:00Z"))
        .title("Title2")
        .build();

    final List<UserTodo> expected = List.of(first, second);

    //when:
    final List<UserTodo> userTodos = this.userRestAdapter.retrieveUserTodos(userId);

    //then:
    assertThat(userTodos)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .isEqualTo(expected);
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
    final List<PostComment> comments = this.userRestAdapter.retrievePostComments(postId);

    //then:
    assertThat(comments)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .isEqualTo(expected);
  }

  @Test
  void shouldRetrieveAllCommentsPages() {
    //given:
    final Long postId = 5678L;

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
    final List<PostComment> comments = this.userRestAdapter.retrievePostComments(postId);

    //then:
    assertThat(comments)
        .isNotNull()
        .isNotEmpty()
        .hasSize(2)
        .isEqualTo(expected);
  }

}