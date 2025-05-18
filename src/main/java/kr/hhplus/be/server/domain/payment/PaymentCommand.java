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
      Long orderId,
      Long userId,
      Long amount
  ) {

    public PaymentSuccessCommand {
      if (orderId == null || userId == null || amount == null) {
        throw new PaymentIllegalStateException("주문 ID, 사용자 ID, 결제 금액은 필수입니다.");
      }
    }

    public static PaymentSuccessCommand of(Long orderId, Long userId, Long finalPrice) {
      return new PaymentSuccessCommand(orderId, userId, finalPrice);
    }
  }
}
