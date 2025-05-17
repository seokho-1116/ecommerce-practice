package kr.hhplus.be.server.support;

import static org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive;
import static org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization;

import java.util.Comparator;
import java.util.List;
import kr.hhplus.be.server.common.exception.ServerException;
import kr.hhplus.be.server.support.SpelExpressionEvaluator.SpelParseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

  private final SpelExpressionEvaluator spelExpressionEvaluator;
  private final RedissonClient redissonClient;

  @SuppressWarnings("unchecked")
  @Around("@annotation(kr.hhplus.be.server.support.DistributedLock)")
  public Object aroundDistributedLock(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    DistributedLock annotation = signature.getMethod().getAnnotation(DistributedLock.class);

    SpelParseRequest request = SpelParseRequest.builder()
        .args(joinPoint.getArgs())
        .parameterNames(signature.getParameterNames())
        .expression(annotation.expression())
        .build();

    List<Object> keys = spelExpressionEvaluator.parse(request, List.class);
    if (keys == null || keys.isEmpty()) {
      throw new IllegalArgumentException("락 키가 비어있습니다.");
    }

    List<String> sortedKeys = keys.stream()
        .map(String::valueOf)
        .sorted(Comparator.naturalOrder())
        .toList();

    RLock multiLock = getMultiLock(sortedKeys, annotation);
    try {
      boolean isAcquired = multiLock.tryLock(annotation.timeout(), annotation.timeUnit());
      if (!isAcquired) {
        throw new ServerException("분산락을 획득하지 못했습니다.");
      }

      return joinPoint.proceed();
    } catch (Throwable e) {
      throw new ServerException(e);
    } finally {
      if (isActualTransactionActive()) {
        registerSynchronization(new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (multiLock.isHeldByCurrentThread()) {
              multiLock.unlock();
              log.debug("Unlocking distributed lock for keys: {}", sortedKeys);
            }
          }
        });
      } else {
        if (multiLock.isHeldByCurrentThread()) {
          multiLock.unlock();
          log.debug("Unlocking distributed lock for keys: {}", sortedKeys);
        }
      }
    }
  }

  private RLock getMultiLock(List<String> keys, DistributedLock annotation) {
    RLock[] locks = new RLock[keys.size()];
    for (int i = 0; i < keys.size(); i++) {
      String stringKey = String.valueOf(keys.get(i));
      LockKey lockKey = annotation.key();

      String generatedKey = lockKey.generate(stringKey);
      locks[i] = redissonClient.getLock(generatedKey);
    }

    return redissonClient.getMultiLock(locks);
  }
}