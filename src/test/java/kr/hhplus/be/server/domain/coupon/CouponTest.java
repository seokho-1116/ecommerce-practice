package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CouponTest {

  @DisplayName("고정된 금액을 할인하는 쿠폰은 고정된 금액을 반환한다.")
  @Test
  void calculateDiscountPriceWhenFixed() {
    // given
    long fixedDiscountAmount = 1000L;
    Coupon coupon = fixed(fixedDiscountAmount);

    // when
    long discountPrice = coupon.calculateDiscountPrice(10000);

    // then
    assertThat(discountPrice).isEqualTo(fixedDiscountAmount);
  }

  private Coupon fixed(long discountAmount) {
    return Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(discountAmount)
        .fromTs(LocalDateTime.now().minusMonths(1))
        .toTs(LocalDateTime.now().plusMonths(1))
        .build();
  }

  @DisplayName("고정된 금액을 할인하는 쿠폰은 전체 금액이 0원일 경우 0원을 반환한다.")
  @Test
  void calculateDiscountPriceWhenFixedAndTotalPriceIsZero() {
    // given
    long fixedDiscountAmount = 1000L;
    Coupon coupon = fixed(fixedDiscountAmount);

    // when
    long discountPrice = coupon.calculateDiscountPrice(0);

    // then
    assertThat(discountPrice).isZero();
  }

  @DisplayName("고정된 금액을 할인하는 쿠폰은 전체 금액이 음수인 경우 0원을 반환한다.")
  @Test
  void calculateDiscountPriceWhenFixedAndTotalPriceIsNegative() {
    // given
    long fixedDiscountAmount = 1000L;
    Coupon coupon = fixed(fixedDiscountAmount);

    // when
    long discountPrice = coupon.calculateDiscountPrice(-10000);

    // then
    assertThat(discountPrice).isZero();
  }

  @DisplayName("고정된 금액을 할인하는 쿠폰은 전체 금액이 쿠폰 할인 금액보다 적을 경우 전체 금액을 반환한다.")
  @Test
  void calculateDiscountPriceWhenFixedAndTotalPriceIsLessThanDiscountAmount() {
    // given
    long fixedDiscountAmount = 1000L;
    Coupon coupon = fixed(fixedDiscountAmount);

    // when
    long discountPrice = coupon.calculateDiscountPrice(500);

    // then
    assertThat(discountPrice).isEqualTo(500L);
  }

  @DisplayName("비율로 할인하는 쿠폰은 비율에 맞게 할인 금액을 반환한다.")
  @Test
  void calculateDiscountPriceWhenPercentage() {
    // given
    double discountRate = 0.2;
    Coupon coupon = percentage(discountRate);

    // when
    long totalPrice = 10000;
    long discountPrice = coupon.calculateDiscountPrice(totalPrice);

    // then
    assertThat(discountPrice).isEqualTo((long) (totalPrice * discountRate));
  }

  private Coupon percentage(double discountRate) {
    return Coupon.builder()
        .couponType(CouponType.PERCENTAGE)
        .discountRate(discountRate)
        .fromTs(LocalDateTime.now().minusMonths(1))
        .toTs(LocalDateTime.now().plusMonths(1))
        .build();
  }

  @DisplayName("비율로 할인하는 쿠폰은 전체 금액이 0원 이하일 경우 0원을 반환한다.")
  @Test
  void calculateDiscountPriceWhenPercentageAndTotalPriceIsZeroOrLess() {
    // given
    double discountRate = 0.1;
    Coupon coupon = percentage(discountRate);

    // when
    long discountPrice = coupon.calculateDiscountPrice(0);

    // then
    assertThat(discountPrice).isZero();
  }

  @DisplayName("비율로 할인하는 쿠폰은 전체 금액이 음수인 경우 0원을 반환한다.")
  @Test
  void calculateDiscountPriceWhenPercentageAndTotalPriceIsNegative() {
    // given
    double discountRate = 0.1;
    Coupon coupon = percentage(discountRate);

    // when
    long discountPrice = coupon.calculateDiscountPrice(-10000);

    // then
    assertThat(discountPrice).isZero();
  }
}