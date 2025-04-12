package kr.hhplus.be.server.interfaces.coupon;

public record CouponIssueRequest(
    Long userId,
    Long couponId
) {

}
