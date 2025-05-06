package kr.hhplus.be.server.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private RedissonClient redissonClient;

  @Mock
  private MethodSignature methodSignature;

  @Mock
  private RLock rLock;

  @InjectMocks
  private DistributedLockAspect distributedLockAspect;

  @Test
  @DisplayName("AOP가 정상적으로 동작해야 한다")
  void testAroundDistributedLock() throws Throwable {
    // given
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(joinPoint.getArgs()).thenReturn(new Object[]{1L, 1L});

    Method method = DistributedLockAspectTest.class.getDeclaredMethod("test", Long.class, Long.class);
    when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
    when(methodSignature.getMethod()).thenReturn(method);

    when(redissonClient.getMultiLock(any())).thenReturn(rLock);
    when(rLock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);

    // when
    distributedLockAspect.aroundDistributedLock(joinPoint);

    // then
    verify(joinPoint, atLeastOnce()).proceed();
  }

  @DistributedLock(
      key = CacheKey.COUPON_ISSUE,
      expression = "#param1",
      timeout = 1000,
      timeUnit = TimeUnit.MILLISECONDS
  )
  void test(Long param1, Long param2) {
    // do nothing
  }
}