package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;

public record OrderResult(
    UserCouponInfo userCoupon,
    OrderInfo order
) {

  public static OrderResult of(OrderInfo order, UserCouponInfo userCoupon) {
    return new OrderResult(userCoupon, order);
  }
}
