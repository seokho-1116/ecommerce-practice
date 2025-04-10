package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;

public record OrderResult(
    UserCoupon userCoupon,
    Order order
) {

  public static OrderResult of(Order order, UserCoupon userCoupon) {
    return new OrderResult(userCoupon, order);
  }
}
