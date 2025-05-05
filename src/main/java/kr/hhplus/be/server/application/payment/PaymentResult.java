package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;

public record PaymentResult(
    OrderInfo order,
    long remainingPoint
) {

  public static PaymentResult of(OrderInfo order, long remainingPoint) {
    return new PaymentResult(
        order,
        remainingPoint
    );
  }
}
