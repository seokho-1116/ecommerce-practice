package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.coupon.CouponEventPublisher;
import kr.hhplus.be.server.support.CacheKeyHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class CouponIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private CouponService couponService;

  @MockitoBean
  private CouponEventPublisher couponEventPublisher;

  @Autowired
  private CouponRepository couponRepository;

  private User user;
  private User user2;
  private Coupon coupon;
  private Coupon notIssuedCoupon;
  private final Long orderId = 100L;

  @BeforeEach
  void setup() {
    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    user2 = userTestDataGenerator.user();
    testHelpRepository.save(user2);

    coupon = couponTestDataGenerator.validCoupon();
    TestReflectionUtil.setField(coupon, "quantity", 10L);
    testHelpRepository.save(coupon);

    notIssuedCoupon = couponTestDataGenerator.validCoupon();
    TestReflectionUtil.setField(notIssuedCoupon, "quantity", 1L);
    TestReflectionUtil.setField(notIssuedCoupon, "couponStatus", CouponStatus.AVAILABLE);
    testHelpRepository.save(notIssuedCoupon);

    UserCoupon userCoupon = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    TestReflectionUtil.setField(userCoupon, "orderId", orderId);
    testHelpRepository.save(userCoupon);

    UserCoupon userCoupon2 = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    TestReflectionUtil.setField(userCoupon2, "orderId", orderId);
    testHelpRepository.save(userCoupon2);
  }

  @DisplayName("쿠폰을 사용하면 해당 사용자의 주문에 연결된 모든 쿠폰이 사용된다")
  @Test
  void useTest() {
    // given
    Long userId = user.getId();

    // when
    couponService.use(userId, orderId);

    // then
    List<UserCoupon> userCoupons = couponRepository.findAllUserCouponsByUserIdAndOrderId(userId, orderId);
    assertThat(userCoupons)
        .isNotEmpty()
        .allMatch(UserCoupon::getIsUsed);
  }

  @DisplayName("동시에 쿠폰을 사용하면 쿠폰이 한 번만 사용된다")
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
          couponService.use(user.getId(), orderId);
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
    // 동시 요청 중 하나만 성공해야 함 (이미 사용된 쿠폰으로 인해)
    List<UserCoupon> userCoupons = couponRepository.findAllUserCouponsByUserIdAndOrderId(user.getId(), orderId);
    assertThat(userCoupons)
        .isNotEmpty()
        .allMatch(UserCoupon::getIsUsed);
    assertThat(successCount.get()).isEqualTo(1);
  }

  @DisplayName("쿠폰이 캐시에 없을 때 쿠폰을 조회하면 DB에서 저장 후 캐시에 저장한다")
  @Test
  void findByIdInCacheTest() {
    // given
    Long couponId = coupon.getId();

    // when
    CouponIssueInfo result = couponService.issue(user.getId(), couponId);

    // then
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON.value(couponId);
    Coupon saveInCache = testHelpRepository.findInCache(key.generate(), new TypeReference<>() {
    });
    assertThat(result).isNotNull();
    assertThat(saveInCache.getId()).isEqualTo(couponId);
  }

  @DisplayName("주문 ID가 없는 사용자의 쿠폰을 사용하려고 하면 아무 쿠폰도 사용되지 않는다")
  @Test
  void useTestWithNoOrderId() {
    // given
    Long userId = user.getId();
    Long nonExistentOrderId = 999L;

    // when
    couponService.use(userId, nonExistentOrderId);

    // then
    List<UserCoupon> userCoupons = couponRepository.findAllUserCouponsByUserIdAndOrderId(userId, orderId);
    assertThat(userCoupons).noneMatch(UserCoupon::getIsUsed);
  }
}