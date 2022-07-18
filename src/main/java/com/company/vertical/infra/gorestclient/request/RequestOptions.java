package com.company.vertical.infra.gorestclient.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "gorest-client.request-options")
public class RequestOptions {

  private String baseUrl;

}