package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import kr.hhplus.be.server.support.CacheKeyHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CouponIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private CouponService couponService;

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private CouponTestDataGenerator couponTestDataGenerator;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  private UserCoupon userCoupon;
  private User user;
  private User user2;
  private Coupon coupon;
  private Coupon notIssuedCoupon;

  @BeforeEach
  void setup() {
    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    user2 = userTestDataGenerator.user();
    testHelpRepository.save(user2);

    coupon = couponTestDataGenerator.validateCoupon();
    TestReflectionUtil.setField(coupon, "quantity", 10L);
    testHelpRepository.save(coupon);

    notIssuedCoupon = couponTestDataGenerator.validateCoupon();
    TestReflectionUtil.setField(notIssuedCoupon, "quantity", 1L);
    TestReflectionUtil.setField(notIssuedCoupon, "couponStatus", CouponStatus.AVAILABLE);
    testHelpRepository.save(notIssuedCoupon);

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

  @DisplayName("쿠폰을 발급을 요청하면 쿠폰이 발급 요청이 수행된다")
  @Test
  void issueTest() {
    // given
    // when
    CouponIssueInfo issuedCoupon = couponService.issue(user.getId(), coupon.getId());

    // then
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON_EVENT_QUEUE.value(coupon.getId());
    Set<Long> userIds = testHelpRepository.findZsetInCache(key.generate(), 0,
        100, new TypeReference<>() {
        });
    assertThat(issuedCoupon).isNotNull();
    assertThat(userIds).contains(user.getId());
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

  @DisplayName("동시에 쿠폰을 발급하면 쿠폰이 발급 요청된다")
  @Test
  void concurrentIssueTest() throws InterruptedException {
    // given
    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    new Thread(() -> {
      try {
        couponService.issue(user.getId(), coupon.getId());
      } catch (Exception ignore) {
        // ignore
      } finally {
        latch.countDown();
      }
    }).start();

    new Thread(() -> {
      try {
        couponService.issue(user2.getId(), coupon.getId());
      } catch (Exception ignore) {
        // ignore
      } finally {
        latch.countDown();
      }
    }).start();

    latch.await();

    // then
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON_EVENT_QUEUE.value(coupon.getId());
    Set<Long> userIds = testHelpRepository.findZsetInCache(key.generate(), 0,
        100, new TypeReference<>() {
        });
    assertThat(userIds).hasSize(concurrentRequest);
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

  @DisplayName("이벤트 쿠폰이 없으면 반환된다")
  @Test
  void findEventCouponTest() {
    // given
    couponService.issue(user2.getId(), notIssuedCoupon.getId());

    // when
    couponService.issueCouponFromQueue();

    // then
    List<UserCoupon> userCoupons = couponRepository.findUserCouponsByUserId(user2.getId());
    assertThat(userCoupons).isEmpty();
  }

  @DisplayName("이벤트 쿠폰 발급 요청 시 쿠폰이 발급 상태인 경우 쿠폰이 발급된다")
  @Test
  void issueAvailableCouponsTest() {
    // given
    couponService.issue(user2.getId(), notIssuedCoupon.getId());

    // when
    couponService.issueCouponFromQueue();

    // then
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON_EVENT_QUEUE.value(notIssuedCoupon.getId());
    Set<Long> userIds = testHelpRepository.findZsetInCache(key.generate(), 0, 100, new TypeReference<>() {
        });
    assertThat(userIds).contains(user2.getId());
  }

  @DisplayName("이벤트 쿠폰 발급 요청 시 쿠폰이 발급 완료 상태에 도달된 경우 상태가 변경된다")
  @Test
  void issueAvailableCouponsWithCompletedStatusTest() {
    // given
    couponService.issue(user.getId(), notIssuedCoupon.getId());
    couponRepository.saveEventCoupon(notIssuedCoupon);

    // when
    couponService.issueCouponFromQueue();

    // then
    Coupon saved = couponRepository.findById(notIssuedCoupon.getId())
        .orElseThrow(RuntimeException::new);
    assertThat(saved.getCouponStatus()).isEqualTo(CouponStatus.COMPLETE);
  }

  @DisplayName("같은 유저가 동시에 쿠폰을 발급하면 쿠폰이 오직 한 장만 발급된다")
  @Test
  void concurrentIssueWithLimitedQuantityTest() throws InterruptedException {
    // given
    couponRepository.saveEventCoupon(notIssuedCoupon);
    couponService.issue(user2.getId(), notIssuedCoupon.getId());
    couponService.issue(user2.getId(), notIssuedCoupon.getId());

    int concurrentRequest = 2;
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          couponService.issueCouponFromQueue();
        } catch (Exception ignore) {
          // ignore
        } finally {
          latch.countDown();
        }
      }).start();
    }

    latch.await();

    List<UserCoupon> userCoupons2 = couponRepository.findUserCouponsByUserId(user2.getId());
    assertThat(userCoupons2).hasSize(1);
  }
}
