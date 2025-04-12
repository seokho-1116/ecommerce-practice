package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.payment.PaymentCommand;

public record OrderPaymentRequest(
    Long userId
) {

  public PaymentCommand toCommand(long orderId) {
    return new PaymentCommand(
        userId,
        orderId
    );
  }
}