package kr.hhplus.be.server.application.coupon;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponTestDataGenerator;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CouponFacadeTest extends IntegrationTestSupport {

  @Autowired
  private CouponFacade couponFacade;

  @Autowired
  private CouponTestDataGenerator couponTestDataGenerator;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  private User user;
  private User user2;
  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = couponTestDataGenerator.validLimitedCoupon(1L);
    testHelpRepository.save(coupon);

    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    user2 = userTestDataGenerator.user();
    testHelpRepository.save(user2);
  }

  @DisplayName("개수가 한정된 선착순 쿠폰을 동시에 발급하면 가장 먼저 발급 요청한 사람이 쿠폰을 발급받는다.")
  @Test
  void issueFirstComeFirstServedCoupon() throws InterruptedException {
    // given
    CountDownLatch latch = new CountDownLatch(2);

    // when
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);
    new Thread(() -> {
      try {
        couponFacade.issue(this.user.getId(), coupon.getId());

        successCount.incrementAndGet();
      } catch (Exception e) {
        failCount.incrementAndGet();
      } finally {
        latch.countDown();
      }
    }).start();

    new Thread(() -> {
      try {
        couponFacade.issue(this.user2.getId(), coupon.getId());

        successCount.incrementAndGet();
      } catch (Exception e) {
        failCount.incrementAndGet();
      } finally {
        latch.countDown();
      }
    }).start();

    latch.await();

    // then
    assertThat(successCount.get()).isEqualTo(1);
    assertThat(failCount.get()).isEqualTo(1);
  }
}