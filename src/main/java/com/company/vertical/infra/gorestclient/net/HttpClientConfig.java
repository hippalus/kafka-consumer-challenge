package com.company.vertical.infra.gorestclient.net;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class HttpClientConfig {

  private static final int CONNECT_TIMEOUT = 60000;
  private static final int KEEP_ALIVE = 20000;
  private static final int IDLE_TIMEOUT = 10000;
  private static final int INITIAL_DELAY = 30000;
  private static final int IDLE_CONN_CLOSE_PERIOD = INITIAL_DELAY / 2;
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplateBuilder()
        .requestFactory(this::clientHttpRequestFactory)
        .build();
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    final var factory = new HttpComponentsClientHttpRequestFactory();
    factory.setHttpClient(this.httpClient());
    return factory;

  }

  @Bean
  public CloseableHttpClient httpClient() {
    final var connectionManager = this.poolingConnectionManager();
    this.prepareSchedulerForIdleConnections(connectionManager);

    return HttpClients.custom()
        .setDefaultRequestConfig(this.requestConfig())
        .setConnectionManager(connectionManager)
        .setKeepAliveStrategy(this.connectionKeepAliveStrategy())
        .build();
  }

  @Bean
  public RequestConfig requestConfig() {
    return RequestConfig.custom()
        .setContentCompressionEnabled(true)
        .setConnectionRequestTimeout(CONNECT_TIMEOUT)
        .setConnectTimeout(CONNECT_TIMEOUT)
        .setSocketTimeout(CONNECT_TIMEOUT)
        .build();
  }

  @Bean
  public PoolingHttpClientConnectionManager poolingConnectionManager() {
    final var sslContextBuilder = SSLContextBuilder.create();
    try {
      sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
    } catch (final NoSuchAlgorithmException | KeyStoreException e) {
      log.warn(e.getMessage());
    }

    final SSLConnectionSocketFactory sslConnectionSocketFactory = this.getSslConnectionSocketFactory(sslContextBuilder);

    final var connSocketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create();
    connSocketFactoryRegistry.register("http", new PlainConnectionSocketFactory());

    if (Objects.nonNull(sslConnectionSocketFactory)) {
      connSocketFactoryRegistry.register("https", sslConnectionSocketFactory);
    }

    final var poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(connSocketFactoryRegistry.build());
    poolingHttpClientConnectionManager.setMaxTotal(20);

    return poolingHttpClientConnectionManager;
  }

  @Bean
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
    return (httpResponse, httpContext) -> {
      final var it = new BasicHeaderElementIterator(httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        final HeaderElement headerElement = it.nextElement();
        if (headerElement.getName() != null && headerElement.getValue().equalsIgnoreCase("timeout")) {
          return Long.parseLong(headerElement.getValue()) * 1000;
        }
      }
      return KEEP_ALIVE;
    };
  }

  private SSLConnectionSocketFactory getSslConnectionSocketFactory(final SSLContextBuilder sslContextBuilder) {
    try {
      return new SSLConnectionSocketFactory(sslContextBuilder.build());
    } catch (final NoSuchAlgorithmException | KeyManagementException e) {
      log.warn(e.getMessage());
    }
    return null;
  }

  private void prepareSchedulerForIdleConnections(final PoolingHttpClientConnectionManager connectionManager) {
    this.executor.scheduleAtFixedRate(
        this.closeExpiredConnections(connectionManager),
        INITIAL_DELAY,
        IDLE_CONN_CLOSE_PERIOD,
        TimeUnit.MILLISECONDS
    );
  }

  private Runnable closeExpiredConnections(final PoolingHttpClientConnectionManager cm) {
    return () -> {
      cm.closeExpiredConnections();
      cm.closeIdleConnections(IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
      log.trace("Expired and idle connections closed");
    };
  }
}
