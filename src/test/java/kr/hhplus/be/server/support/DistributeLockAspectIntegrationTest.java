package kr.hhplus.be.server.support;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;

class DistributeLockAspectIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private DistributedLockAspect distributedLockAspect;

  private TestService testService;

  @BeforeEach
  void setup() {
    testService = new TestService();
    AspectJProxyFactory factory = new AspectJProxyFactory(testService);
    factory.addAspect(distributedLockAspect);

    testService = factory.getProxy();
  }

  @DisplayName("분산락 AOP가 정상 동작하여 값을 순차적으로 증가시킨다")
  @Test
  void testDistributedLock() throws InterruptedException {
    // given
    Long key = 1L;
    CountDownLatch latch = new CountDownLatch(2);
    int concurrentThreadCount = 2;

    // when
    for (int i = 0; i < concurrentThreadCount; i++) {
      new Thread(() -> {
        try {
          testService.testMethod(key);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    assertThat(testService.getGlobalValue()).isEqualTo(concurrentThreadCount);
  }

  @DisplayName("AOP가 적용되지 않은 메서드는 분산락을 사용하지 않는다")
  @Test
  void testNotAop() throws InterruptedException {
    // given
    Long key = 1L;
    CountDownLatch startLatch = new CountDownLatch(1);
    int concurrentThreadCount = 2;
    CountDownLatch latch = new CountDownLatch(concurrentThreadCount);

    // when
    for (int i = 0; i < concurrentThreadCount; i++) {
      new Thread(() -> {
        try {
          startLatch.await();

          testService.notAop(key);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          latch.countDown();
        }
      }).start();
    }
    startLatch.countDown();
    latch.await();

    // then
    assertThat(testService.getGlobalValue()).isNotEqualTo(concurrentThreadCount);
  }

  @DisplayName("잘못된 키를 사용한 경우 예외가 발생한다")
  @Test
  void testWrongKey() {
    // given
    Long key = 1L;

    // when
    // then
    assertThatThrownBy(() -> testService.wrongKey(key))
        .isInstanceOf(IllegalArgumentException.class);
  }

  static class TestService {

    private Long globalValue = 0L;

    @DistributedLock(
        name = LockKey.COUPON_ISSUE,
        key = "#key",
        timeout = 1000L,
        timeUnit = TimeUnit.MILLISECONDS
    )
    public void testMethod(Long key) throws InterruptedException {
      long value = globalValue;
      Thread.sleep(10);
      globalValue = value + 1;
    }

    @DistributedLock(
        name = LockKey.COUPON_ISSUE,
        key = "#dummy",
        timeout = 1000L,
        timeUnit = TimeUnit.MILLISECONDS
    )
    public void wrongKey(Long key) throws InterruptedException {
      long value = globalValue;
      Thread.sleep(10);
      globalValue = value + 1;
    }

    public void notAop(Long key) throws InterruptedException {
      long value = globalValue;
      Thread.sleep(10);
      globalValue = value + 1;
    }

    public Long getGlobalValue() {
      return globalValue;
    }
  }
}
