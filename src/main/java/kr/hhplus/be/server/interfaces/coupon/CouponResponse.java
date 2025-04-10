package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;

public class CouponResponse {

  public record CouponIssueResponse(
      @Schema(description = "유저 ID")
      Long userId,

      @Schema(description = "쿠폰 ID")
      Long couponId,

      @Schema(description = "유저 쿠폰 ID")
      Long userCouponId,

      @Schema(description = "쿠폰 이름")
      String couponName,

      @Schema(description = "할인율")
      Double discountRate,

      @Schema(description = "할인 금액")
      Long discountAmount,

      @Schema(description = "쿠폰 타입")
      CouponType couponType,

      @Schema(description = "쿠폰 사용 가능 시작일")
      LocalDateTime from,

      @Schema(description = "쿠폰 사용 가능 종료일")
      LocalDateTime to,

      @Schema(description = "쿠폰 발급일")
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
      @Schema(description = "쿠폰 ID")
      Long couponId,

      @Schema(description = "쿠폰 이름")
      String couponName,

      @Schema(description = "할인율")
      Double discountRate,

      @Schema(description = "할인 금액")
      Long discountAmount,

      @Schema(description = "쿠폰 타입")
      CouponType couponType,

      @Schema(description = "쿠폰 사용 가능 시작일")
      LocalDateTime from,

      @Schema(description = "쿠폰 사용 가능 종료일")
      LocalDateTime to,

      @Schema(description = "쿠폰 발급일")
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
