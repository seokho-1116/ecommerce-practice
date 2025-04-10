package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentIllegalStateException;

public record PaymentCommand(
    Long userId,
    Long orderId
) {

  public PaymentCommand {
    if (userId == null || orderId == null) {
      throw new PaymentIllegalStateException("사용자 ID와 주문 ID는 필수입니다.");
    }
  }
}
