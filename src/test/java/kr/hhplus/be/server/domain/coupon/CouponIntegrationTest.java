package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CouponIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponTestDataGenerator couponTestDataGenerator;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  private UserCoupon userCoupon;
  private User user;
  private Coupon coupon;

  @BeforeEach
  void setup() {
    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    coupon = couponTestDataGenerator.validateCoupon();
    long randomQuantity = ThreadLocalRandom.current().nextLong(1, 6);
    TestReflectionUtil.setField(coupon, "quantity", randomQuantity);
    testHelpRepository.save(coupon);

    userCoupon = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    testHelpRepository.save(userCoupon);
  }

  @DisplayName("쿠폰을 사용하면 쿠폰이 사용된다")
  @Test
  void useTest() {
    // given
    // when
    couponService.use(userCoupon);

    // then
    assertThat(userCoupon.getIsUsed()).isTrue();
  }

  @DisplayName("쿠폰을 발급하면 쿠폰이 발급된다")
  @Test
  void issueTest() {
    // given
    // when
    UserCoupon issuedCoupon = couponService.issue(user, coupon.getId());

    // then
    assertThat(issuedCoupon.getIsUsed()).isFalse();
  }

  @DisplayName("동시에 쿠폰을 사용하면 쿠폰이 한 장만 사용된다")
  @Test
  void concurrentUseTest() throws InterruptedException {
    // given
    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          couponService.use(userCoupon);

          successCount.incrementAndGet();
        } catch (Exception e) {
          failureCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    assertThat(successCount.get()).isEqualTo(1);
  }

  @DisplayName("동시에 쿠폰을 발급하면 쿠폰이 수량만큼만 발급된다")
  @Test
  void concurrentIssueTest() throws InterruptedException {
    // given
    int concurrentRequest = 10;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failureCount = new AtomicInteger(0);
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          couponService.issue(user, coupon.getId());
          successCount.incrementAndGet();
        } catch (Exception e) {
          failureCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    assertThat(successCount.longValue()).isEqualTo(coupon.getQuantity());
  }
}
