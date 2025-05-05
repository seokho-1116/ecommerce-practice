package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
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
    TestReflectionUtil.setField(coupon, "quantity", 10L);
    testHelpRepository.save(coupon);

    userCoupon = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    testHelpRepository.save(userCoupon);
  }

  @DisplayName("쿠폰을 사용하면 쿠폰이 사용된다")
  @Test
  void useTest() {
    // given
    Long userCouponId = userCoupon.getId();

    // when
    couponService.use(userCouponId);

    // then
    UserCouponInfo result = couponService.findUserCouponByUserCouponId(userCouponId);
    assertThat(result.isUsed()).isTrue();
  }

  @DisplayName("쿠폰을 발급하면 쿠폰이 발급된다")
  @Test
  void issueTest() {
    // given
    // when
    UserCouponInfo issuedCoupon = couponService.issue(user, coupon.getId());

    // then
    assertThat(issuedCoupon.isUsed()).isFalse();
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
          Long userCouponId = userCoupon.getId();

          couponService.use(userCouponId);

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
    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          couponService.issue(user, coupon.getId());
        } catch (Exception ignore) {
          // ignore
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    Long couponQuantity = couponService.findAllCoupons().stream()
        .filter(issue -> issue.getId().equals(coupon.getId()))
        .map(Coupon::getQuantity)
        .findAny()
        .orElse(-1L);
    assertThat(couponQuantity).isEqualTo(coupon.getQuantity() - concurrentRequest);
  }
}
