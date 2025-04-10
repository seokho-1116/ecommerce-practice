package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.Coupon.CouponBuilder;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponIllegalStateException;
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

  @DisplayName("시작일이 null이면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithNullFromTsTest() {
    // given
    LocalDateTime toTs = LocalDateTime.now().plusMonths(1);
    CouponBuilder couponBuilder = Coupon.builder()
        .fromTs(null)
        .toTs(toTs);

    // when
    // then
    assertThatThrownBy(couponBuilder::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("종료일이 null이면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithNullToTsTest() {
    // given
    LocalDateTime fromTs = LocalDateTime.now().minusMonths(1);
    CouponBuilder couponBuilderoupon = Coupon.builder()
        .fromTs(fromTs)
        .toTs(null);

    // when
    // then
    assertThatThrownBy(couponBuilderoupon::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("시작일이 종료일보다 늦으면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithFromTsAfterToTsTest() {
    // given
    LocalDateTime fromTs = LocalDateTime.now().plusMonths(1);
    LocalDateTime toTs = LocalDateTime.now().minusMonths(1);
    CouponBuilder couponBuilder = Coupon.builder()
        .fromTs(fromTs)
        .toTs(toTs);

    // when
    // then
    assertThatThrownBy(couponBuilder::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 타입이 null이면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithNullCouponTypeTest() {
    // given
    LocalDateTime fromTs = LocalDateTime.now().minusMonths(1);
    LocalDateTime toTs = LocalDateTime.now().plusMonths(1);
    CouponBuilder couponBuilder = Coupon.builder()
        .fromTs(fromTs)
        .toTs(toTs)
        .couponType(null);

    // when
    // then
    assertThatThrownBy(couponBuilder::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 타입이 비율 할인일 때 할인율이 0보다 작으면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithNegativeDiscountRateTest() {
    // given
    LocalDateTime fromTs = LocalDateTime.now().minusMonths(1);
    LocalDateTime toTs = LocalDateTime.now().plusMonths(1);
    CouponBuilder couponBuilder = Coupon.builder()
        .fromTs(fromTs)
        .toTs(toTs)
        .couponType(CouponType.PERCENTAGE)
        .discountRate(-0.1);

    // when
    // then
    assertThatThrownBy(couponBuilder::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }

  @DisplayName("쿠폰 타입이 정액 할인일 때 할인 금액이 0보다 작으면 쿠폰 상태 예외가 발생한다")
  @Test
  void createCouponWithNegativeDiscountAmountTest() {
    // given
    LocalDateTime fromTs = LocalDateTime.now().minusMonths(1);
    LocalDateTime toTs = LocalDateTime.now().plusMonths(1);
    CouponBuilder couponBuilder = Coupon.builder()
        .fromTs(fromTs)
        .toTs(toTs)
        .couponType(CouponType.FIXED)
        .discountAmount(-1000L);

    // when
    // then
    assertThatThrownBy(couponBuilder::build)
        .isInstanceOf(CouponIllegalStateException.class);
  }
}