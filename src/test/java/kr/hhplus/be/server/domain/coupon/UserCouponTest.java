package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponIllegalStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserCouponTest {

  @DisplayName("쿠폰 사용 시 쿠폰 상태가 사용 가능 상태에서 사용 완료 상태로 변경된다")
  @Test
  void useTest() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .isUsed(false)
        .coupon(coupon)
        .build();

    // when
    userCoupon.use();

    // then
    assertThat(userCoupon.getIsUsed()).isTrue();
  }

  @DisplayName("쿠폰 사용 시 쿠폰이 사용 상태면 쿠폰 상태 예외가 발생한다")
  @Test
  void useTestWhenAlreadyUsed() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .isUsed(true)
        .coupon(coupon)
        .build();

    // when & then
    assertThatThrownBy(userCoupon::use)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 사용 시 쿠폰이 비활성 상태면 쿠폰 상태 예외가 발생한다")
  @Test
  void useTestWhenCouponInactive() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();
    coupon.deactivate();

    UserCoupon userCoupon = UserCoupon.builder()
        .isUsed(false)
        .coupon(coupon)
        .build();

    // when & then
    assertThatThrownBy(userCoupon::use)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 사용 시 쿠폰 사용 기간이 만료된 경우 쿠폰 상태 예외가 발생한다")
  @Test
  void useTestWhenCouponExpired() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(now.minusDays(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .isUsed(false)
        .coupon(coupon)
        .build();

    // when & then
    assertThatThrownBy(userCoupon::use)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 사용 기한이 오늘까지면 쿠폰 사용 가능하다")
  @Test
  void useTestWhenCouponToday() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(LocalDate.now().atTime(LocalTime.MAX))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .isUsed(false)
        .coupon(coupon)
        .build();

    // when
    userCoupon.use();

    // then
    assertThat(userCoupon.getIsUsed()).isTrue();
  }
}