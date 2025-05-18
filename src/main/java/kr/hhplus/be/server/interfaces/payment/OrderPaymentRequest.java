package kr.hhplus.be.server.interfaces.payment;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;

public record OrderPaymentRequest(
    @NotNull
    Long userId
) {

  public OrderPaymentCommand toCommand(long orderId) {
    return new OrderPaymentCommand(
        userId,
        orderId
    );
  }
}