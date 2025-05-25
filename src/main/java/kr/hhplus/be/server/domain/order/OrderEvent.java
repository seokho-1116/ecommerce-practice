package kr.hhplus.be.server.domain.order;

import java.util.Map;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;

public class OrderEvent {

  private OrderEvent() {
  }

  public record OrderSuccessEvent(
      Long orderId,
      Long userId,
      Long userCouponId
  ) {


    public static OrderSuccessEvent from(OrderInfo order, UserCouponInfo userCoupon) {
      return new OrderSuccessEvent(
          order.id(),
          order.userId(),
          userCoupon != null ? userCoupon.id() : null
      );
    }
  }

  public record OrderPaymentSuccessEvent(
      Long orderId,
      Long userId,
      Long finalPrice,
      Map<Long, Long> productOptionIdAmountMap
  ) {

    public static OrderPaymentSuccessEvent from(Order order,
        Map<Long, Long> productOptionIdAmountMap) {
      return new OrderPaymentSuccessEvent(
          order.getId(),
          order.getUserId(),
          order.getFinalPrice(),
          productOptionIdAmountMap
      );
    }
  }

  public record UseCouponEvent(
      Long userCouponId
  ) {

  }
}
