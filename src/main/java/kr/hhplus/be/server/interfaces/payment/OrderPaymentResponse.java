package kr.hhplus.be.server.interfaces.payment;

import java.time.LocalDateTime;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.domain.order.OrderStatus;

public record OrderPaymentResponse(
    Long orderId,
    Long userId,
    Long amount,
    OrderStatus status,
    Long remainingPoint,
    LocalDateTime createdAt
) {

  public static OrderPaymentResponse from(PaymentResult paymentResult) {
    return new OrderPaymentResponse(
        paymentResult.order().getId(),
        paymentResult.order().getUser().getId(),
        paymentResult.order().getTotalPrice(),
        paymentResult.order().getStatus(),
        paymentResult.remainingPoint(),
        LocalDateTime.now()
    );
  }
}
