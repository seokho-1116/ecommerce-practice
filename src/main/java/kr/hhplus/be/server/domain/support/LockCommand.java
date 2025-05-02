package kr.hhplus.be.server.domain.support;

import java.util.List;
import java.util.concurrent.TimeUnit;

public record LockCommand(
    String topic,
    long timeout,
    TimeUnit timeUnit,
    List<String> keys
) {

  public static LockCommand of(String topic, long timeout, TimeUnit timeUnit, List<String> keys) {
    return new LockCommand(
        topic,
        timeout,
        timeUnit,
        keys
    );
  }
}
