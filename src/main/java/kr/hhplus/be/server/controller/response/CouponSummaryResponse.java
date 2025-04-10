package kr.hhplus.be.server.controller.response;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.CouponType;

public record CouponSummaryResponse(
    Long userId,
    Long userCouponId,
    String couponName,
    Double discountRate,
    Long discountAmount,
    CouponType couponType,
    LocalDateTime from,
    LocalDateTime to,
    LocalDateTime createdAt
) {

}