package kr.hhplus.be.server.infrastructure.support;

import java.util.List;
import java.util.function.Supplier;
import kr.hhplus.be.server.common.exception.InternalServerException;
import kr.hhplus.be.server.domain.support.LockCommand;
import kr.hhplus.be.server.domain.support.LockTemplate;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DistributeLockImpl implements LockTemplate {

  private static final String LOCK_KEY_PREFIX = "lock:";

  private final RedissonClient redissonClient;

  @Override
  public <T> T execute(Supplier<T> supplier, LockCommand lockCommand) {
    List<String> keys = lockCommand.keys();
    RLock[] locks = new RLock[keys.size()];
    for (int i = 0; i < keys.size(); i++) {
      String lockKey = LOCK_KEY_PREFIX + lockCommand.keys() + ":" + keys.get(i);
      locks[i] = redissonClient.getLock(lockKey);
    }

    RLock multiLock = redissonClient.getMultiLock(locks);

    try {
      boolean isAcquired = multiLock.tryLock(lockCommand.timeout(), lockCommand.timeUnit());

      if (isAcquired) {
        return supplier.get();
      }
    } catch (InterruptedException e) {
      throw new InternalServerException();
    } finally {
      multiLock.unlock();
    }

    return null;
  }
}
