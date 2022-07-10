package com.company.vertical.infra.gorestclient.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configuration
@ConfigurationProperties(prefix = "gorest-client.request-options")
public class RequestOptions {

  // @Value("${gorest-client.request-options.access-token}")
  private String accessToken;

  // @Value("${gorest-client.request-options.encoding}")
  private String encoding;

  // @Value("${gorest-client.request-options.base-url}")
  private String baseUrl;

}