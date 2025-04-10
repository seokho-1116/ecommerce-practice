package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.payment.PaymentCommand;

public record OrderPaymentRequest(
    @NotNull
    Long userId
) {

  public PaymentCommand toCommand(long orderId) {
    return new PaymentCommand(
        userId,
        orderId
    );
  }
}