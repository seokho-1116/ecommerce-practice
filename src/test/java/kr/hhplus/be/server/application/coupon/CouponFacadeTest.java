package kr.hhplus.be.server.application.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "classpath:db/coupon_test_case.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class CouponFacadeTest {

  private static final Logger log = LoggerFactory.getLogger(CouponFacadeTest.class);
  @Autowired
  private CouponFacade couponFacade;

  @DisplayName("쿠폰 발급 시 쿠폰이 발급되어야 한다")
  @Test
  void issueTest() {
    // given
    Long userId = 1L;
    Long couponId = 1L;

    // when
    UserCoupon userCoupon = couponFacade.issue(userId, couponId);

    // then
    assertThat(userCoupon.getIsUsed()).isFalse();
    assertThat(userCoupon.getUser().getId()).isEqualTo(userId);
  }

  @DisplayName("수량이 제한된 쿠폰을 여러 사용자가 발급받을 때 수량만큼 발급되고 나머지는 실패해야 한다")
  @Test
  void issueLimitedCouponTest() throws InterruptedException {
    // given
    Long couponId = 2L;
    long quantity = 5L;
    int userCount = 30;
    CountDownLatch latch = new CountDownLatch(userCount);

    // when
    AtomicLong issuedCount = new AtomicLong(0);
    AtomicLong failedCount = new AtomicLong(0);
    for (int userRequest = 1; userRequest <= userCount; userRequest++) {
      Long userId = (long) userRequest;
      new Thread(() -> {
        try {
          couponFacade.issue(userId, couponId);
          issuedCount.incrementAndGet();
        } catch (Exception e) {
          failedCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }

    latch.await();

    // then
    assertThat(issuedCount.get()).isEqualTo(quantity);
    assertThat(failedCount.get()).isEqualTo(userCount - quantity);
  }
}