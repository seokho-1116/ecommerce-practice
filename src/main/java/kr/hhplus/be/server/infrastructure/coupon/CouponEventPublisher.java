package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEvent;

public interface CouponEventPublisher {

    void issueCoupon(CouponEvent.CouponIssueEvent event);
}
