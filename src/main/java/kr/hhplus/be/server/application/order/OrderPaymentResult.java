package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;

public record OrderPaymentResult(
    Long orderId,
    Long userId,
    Long finalPrice
) {

  public static OrderPaymentResult from(OrderInfo orderResult) {
    return new OrderPaymentResult(
        orderResult.id(),
        orderResult.userId(),
        orderResult.finalPrice()
    );
  }
}
