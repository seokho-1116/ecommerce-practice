package kr.hhplus.be.server.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

@ExtendWith(MockitoExtension.class)
class DistributedLockAspectTest {

  @Mock
  private SpelExpressionEvaluator spelExpressionEvaluator;

  @Mock
  private RedissonClient redissonClient;

  @Mock
  private RLock rLock;

  private TestService testService;

  @BeforeEach
  void setUp() {
    DistributedLockAspect distributedLockAspect = new DistributedLockAspect(spelExpressionEvaluator,
        redissonClient);

    testService = new TestService();
    AspectJProxyFactory factory = new AspectJProxyFactory(testService);
    factory.addAspect(distributedLockAspect);

    testService = factory.getProxy();
  }

  @DisplayName("분산락 AOP가 정상적으로 동작해야 한다")
  @Test
  void testAroundDistributedLock() throws Throwable {
    // given
    when(redissonClient.getMultiLock(any())).thenReturn(rLock);
    when(rLock.tryLock(anyLong(), any())).thenReturn(true);
    when(rLock.isHeldByCurrentThread()).thenReturn(true);
    when(spelExpressionEvaluator.parse(any(), any())).thenReturn(List.of("1"));

    // when
    testService.testMethod(1L, 1L);

    // then
    verify(rLock, times(1)).tryLock(anyLong(), any());
    verify(rLock, times(1)).unlock();
  }

  static class TestService {

    @DistributedLock(
        name = LockKey.COUPON_ISSUE,
        key = "#param1",
        timeout = 1000L,
        timeUnit = TimeUnit.MILLISECONDS
    )
    public void testMethod(Long param1, Long param2) {
      // do nothing
    }
  }
}