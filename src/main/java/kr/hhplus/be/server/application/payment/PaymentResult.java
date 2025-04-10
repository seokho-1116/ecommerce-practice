package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;

public record PaymentResult(
    Order order,
    long remainingPoint
) {

  public static PaymentResult of(Order order, long remainingPoint) {
    return new PaymentResult(
        order,
        remainingPoint
    );
  }
}
