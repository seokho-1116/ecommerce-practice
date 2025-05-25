package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentInfo;

public class PaymentEvent {

  public record PaymentSuccessEvent(
      Long orderId,
      Long userId
  ) {

    public static PaymentSuccessEvent from(
        PaymentInfo paymentInfo) {
      return new PaymentSuccessEvent(
          paymentInfo.orderId(),
          paymentInfo.userId()
      );
    }
  }
}
