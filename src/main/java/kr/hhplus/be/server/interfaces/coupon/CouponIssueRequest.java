package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.constraints.NotNull;

public record CouponIssueRequest(
    @NotNull
    Long userId,

    @NotNull
    Long couponId
) {

}
