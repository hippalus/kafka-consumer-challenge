package com.company.vertical.infra.gorestclient;

import com.company.vertical.infra.gorestclient.adapter.PostAdapter;
import com.company.vertical.infra.gorestclient.adapter.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoRestClient {

  public static final String X_PAGINATION_PAGES = "X-Pagination-Pages";
  private final UserAdapter userAdapter;
  private final PostAdapter postAdapter;

  public UserAdapter users() {
    return this.userAdapter;
  }

  public PostAdapter posts() {
    return this.postAdapter;
  }
}
