package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

  @Mock
  private CouponRepository couponRepository;

  @InjectMocks
  private CouponService couponService;

  @DisplayName("쿠폰 사용 시 해당 사용자의 주문에 연결된 모든 쿠폰이 사용되어야 한다")
  @Test
  void useCouponTest() {
    // given
    Long userId = 1L;
    Long orderId = 100L;

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon1 = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .from(now.minusMonths(1))
        .to(now.plusMonths(1))
        .build();

    Coupon coupon2 = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(2000L)
        .from(now.minusMonths(1))
        .to(now.plusMonths(1))
        .build();

    UserCoupon userCoupon1 = UserCoupon.builder()
        .id(1L)
        .coupon(coupon1)
        .isUsed(false)
        .orderId(orderId)
        .build();

    UserCoupon userCoupon2 = UserCoupon.builder()
        .id(2L)
        .coupon(coupon2)
        .isUsed(false)
        .orderId(orderId)
        .build();

    List<UserCoupon> userCoupons = Arrays.asList(userCoupon1, userCoupon2);

    when(couponRepository.findAllUserCouponsByUserIdAndOrderId(userId, orderId))
        .thenReturn(userCoupons);

    // when
    couponService.use(userId, orderId);

    // then
    verify(couponRepository, times(2)).saveUserCoupon(any(UserCoupon.class));
    assertThat(userCoupon1.getIsUsed()).isTrue();
    assertThat(userCoupon2.getIsUsed()).isTrue();
  }

  @DisplayName("해당 사용자의 주문에 연결된 쿠폰이 없는 경우 아무 작업도 수행하지 않는다")
  @Test
  void useCouponTestWhenNoCouponsFound() {
    // given
    Long userId = 1L;
    Long orderId = 100L;

    when(couponRepository.findAllUserCouponsByUserIdAndOrderId(userId, orderId))
        .thenReturn(Collections.emptyList());

    // when
    couponService.use(userId, orderId);

    // then
    verify(couponRepository, never()).saveUserCoupon(any(UserCoupon.class));
  }

  @DisplayName("단일 쿠폰이 있는 경우 해당 쿠폰만 사용되어야 한다")
  @Test
  void useCouponTestWithSingleCoupon() {
    // given
    Long userId = 1L;
    Long orderId = 100L;

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .from(now.minusMonths(1))
        .to(now.plusMonths(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .isUsed(false)
        .orderId(orderId)
        .build();

    when(couponRepository.findAllUserCouponsByUserIdAndOrderId(userId, orderId))
        .thenReturn(List.of(userCoupon));

    // when
    couponService.use(userId, orderId);

    // then
    verify(couponRepository, times(1)).saveUserCoupon(userCoupon);
    assertThat(userCoupon.getIsUsed()).isTrue();
  }
}