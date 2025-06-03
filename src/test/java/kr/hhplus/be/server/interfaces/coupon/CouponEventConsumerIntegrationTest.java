package kr.hhplus.be.server.interfaces.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponEvent.CouponIssueEvent;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.infrastructure.coupon.CouponEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CouponEventConsumerIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private CouponEventPublisher couponEventPublisher;

  @Autowired
  private CouponRepository couponRepository;

  private Coupon coupon;

  @BeforeEach
  void setup() {
    coupon = couponTestDataGenerator.validCoupon();
    TestReflectionUtil.setField(coupon, "quantity", 10L);
    testHelpRepository.save(coupon);
  }

  @DisplayName("쿠폰 발급 이벤트를 발행하면 쿠폰이 발급된다")
  @Test
  void listenPaymentSuccessEvent() {
    // given
    CouponIssueEvent event = new CouponIssueEvent(1L, coupon.getId());
    couponEventPublisher.issueCoupon(event);

    // when
    // then
    await()
        .pollInterval(Duration.ofSeconds(3))
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          List<UserCoupon> userCoupon = couponRepository.findUserCouponsByCouponId(coupon.getId());

          assertThat(userCoupon).extracting(UserCoupon::getUserId)
              .contains(1L);
        });
  }
}