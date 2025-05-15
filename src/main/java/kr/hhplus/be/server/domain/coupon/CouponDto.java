package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import java.util.function.Function;

public class CouponDto {

  private CouponDto() {}

  public record CouponInfo(
      Long id,
      String name,
      String description,
      Double discountRate,
      Long discountAmount,
      Long quantity,
      CouponType couponType,
      LocalDateTime from,
      LocalDateTime to,
      CouponStatus couponStatus,
      LocalDateTime createdAt
  ) {
    public static CouponInfo from(Coupon coupon) {
      return new CouponInfo(
          coupon.getId(),
          coupon.getName(),
          coupon.getDescription(),
          coupon.getDiscountRate(),
          coupon.getDiscountAmount(),
          coupon.getQuantity(),
          coupon.getCouponType(),
          coupon.getFrom(),
          coupon.getTo(),
          coupon.getCouponStatus(),
          coupon.getCreatedAt()
      );
    }
  }

  public record CouponIssueInfo(
      Long userId,
      Long couponId,
      LocalDateTime issuedAt
  ) {
    public static CouponIssueInfo from(Long userId, Long couponId) {
      return new CouponIssueInfo(userId, couponId, LocalDateTime.now());
    }
  }

  public record UserCouponInfo(
      Long id,
      Long userId,
      Long couponId,
      String couponName,
      Double discountRate,
      Long discountAmount,
      CouponType couponType,
      Boolean isUsed,
      LocalDateTime from,
      LocalDateTime to,
      LocalDateTime createdAt,
      Function<Long, Long> discountCalculator
  ) {

    public static UserCouponInfo from(UserCoupon userCoupon) {
      return new UserCouponInfo(
          userCoupon.getId(),
          userCoupon.getUserId(),
          userCoupon.getCoupon().getId(),
          userCoupon.getCoupon().getName(),
          userCoupon.getCoupon().getDiscountRate(),
          userCoupon.getCoupon().getDiscountAmount(),
          userCoupon.getCoupon().getCouponType(),
          userCoupon.getIsUsed(),
          userCoupon.getCoupon().getFrom(),
          userCoupon.getCoupon().getTo(),
          userCoupon.getCreatedAt(),
          userCoupon::calculateDiscountPrice
      );
    }

    public Long calculateDiscountPrice(Long price) {
      return discountCalculator.apply(price);
    }
  }
}
