package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentInfo;

public record PaymentResult(
    PaymentInfo paymentInfo,
    long remainingPoint
) {

  public static PaymentResult of(PaymentInfo paymentInfo, long remainingPoint) {
    return new PaymentResult(
        paymentInfo,
        remainingPoint
    );
  }
}
