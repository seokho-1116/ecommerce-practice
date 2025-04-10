package kr.hhplus.be.server.interfaces.coupon;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;

public class CouponResponse {

  public record CouponIssueResponse(
      Long userId,
      Long couponId,
      Long userCouponId,
      String couponName,
      Double discountRate,
      Long discountAmount,
      CouponType couponType,
      LocalDateTime from,
      LocalDateTime to,
      LocalDateTime createdAt
  ) {

    public static CouponIssueResponse from(UserCoupon userCoupon) {
      Coupon coupon = userCoupon.getCoupon();

      return new CouponIssueResponse(
          userCoupon.getUser().getId(),
          coupon.getId(),
          userCoupon.getId(),
          coupon.getName(),
          coupon.getDiscountRate(),
          coupon.getDiscountAmount(),
          coupon.getCouponType(),
          coupon.getFromTs(),
          coupon.getToTs(),
          userCoupon.getCreatedAt()
      );
    }
  }

  public record CouponSummaryResponse(
      Long couponId,
      String couponName,
      Double discountRate,
      Long discountAmount,
      CouponType couponType,
      LocalDateTime from,
      LocalDateTime to,
      LocalDateTime createdAt
  ) {

    public static List<CouponSummaryResponse> from(List<Coupon> coupons) {
      return coupons.stream()
          .map(coupon -> new CouponSummaryResponse(
              coupon.getId(),
              coupon.getName(),
              coupon.getDiscountRate(),
              coupon.getDiscountAmount(),
              coupon.getCouponType(),
              coupon.getFromTs(),
              coupon.getToTs(),
              coupon.getCreatedAt()
          ))
          .toList();
    }
  }
}
