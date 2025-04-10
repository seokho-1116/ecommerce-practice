package kr.hhplus.be.server.controller.response;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.CouponType;

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

}
