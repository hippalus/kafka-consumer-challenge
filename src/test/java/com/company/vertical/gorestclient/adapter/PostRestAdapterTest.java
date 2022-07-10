package com.company.vertical.gorestclient.adapter;


import com.company.vertical.infra.gorestclient.adapter.PostAdapter;
import com.company.vertical.infra.gorestclient.request.RequestOptions;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.web.reactive.function.client.WebClient;

class PostRestAdapterTest {

  private MockWebServer mockWebServer;
  private PostAdapter postAdapter;

  @SneakyThrows
  @BeforeEach
  void setupMockWebServer() {
    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();

    final RequestOptions requestOptions = new RequestOptions();
    requestOptions.setBaseUrl(this.mockWebServer.url("").toString());
    requestOptions.setAccessToken("MPjl2Y5AkvtP30rFb3ABRwkYNWsuRhJXkRQLhjaweqsC7H4fg8H2UmY5RpOkbO05");
    requestOptions.setEncoding("gzip");

    this.postAdapter = new PostAdapter(requestOptions, WebClient.create());
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    this.mockWebServer.close();
  }

}