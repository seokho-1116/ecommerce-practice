package kr.hhplus.be.server.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(
    String host,
    int port
) {

  public RedisProperties(String host, int port) {
    if (host == null || host.isBlank()) {
      this.host = "localhost";
    } else {
      this.host = host;
    }

    if (port <= 0) {
      this.port = 6379;
    } else {
      this.port = port;
    }
  }
}
