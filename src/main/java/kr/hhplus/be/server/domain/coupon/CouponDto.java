package kr.hhplus.be.server.domain.coupon;

import java.util.function.Function;

public class CouponDto {

  private CouponDto() {}

  public record UserCouponInfo(
      Long id,
      Long userId,
      Long couponId,
      String couponName,
      Double discountRate,
      Long discountAmount,
      CouponType couponType,
      Boolean isUsed,
      Function<Long, Long> discountCalculator
  ) {

    public static UserCouponInfo from(UserCoupon userCoupon) {
      return new UserCouponInfo(
          userCoupon.getId(),
          userCoupon.getUser().getId(),
          userCoupon.getCoupon().getId(),
          userCoupon.getCoupon().getName(),
          userCoupon.getCoupon().getDiscountRate(),
          userCoupon.getCoupon().getDiscountAmount(),
          userCoupon.getCoupon().getCouponType(),
          userCoupon.getIsUsed(),
          userCoupon::calculateDiscountPrice
      );
    }

    public Long calculateDiscountPrice(Long price) {
      return discountCalculator.apply(price);
    }
  }
}
