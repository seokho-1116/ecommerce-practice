package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentIllegalStateException;

public class PaymentCommand {

  public record OrderPaymentCommand(
      Long userId,
      Long orderId
  ) {

    public OrderPaymentCommand {
      if (userId == null || orderId == null) {
        throw new PaymentIllegalStateException("사용자 ID와 주문 ID는 필수입니다.");
      }
    }
  }

  public record PaymentSuccessCommand(
      Long userId,
      Long orderId,
      Long amount
  ) {

    public PaymentSuccessCommand {
      if (userId == null || orderId == null || amount == null) {
        throw new PaymentIllegalStateException("사용자 ID, 주문 ID, 금액은 필수입니다.");
      }
    }

    public static PaymentSuccessCommand of(Long userId, Long orderId, Long finalPrice) {
      return new PaymentSuccessCommand(userId, orderId, finalPrice);
    }
  }
}
