package kr.hhplus.be.server.domain.payment;

import java.time.LocalDateTime;

public class PaymentDto {

  public record PaymentInfo(
      Long id,
      Long userId,
      Long orderId,
      Long amount,
      PaymentStatus status,
      LocalDateTime createdAt
  ) {

    public static PaymentInfo from(Payment payment) {
      return new PaymentInfo(
          payment.getId(),
          payment.getUserId(),
          payment.getOrderId(),
          payment.getAmount(),
          payment.getStatus(),
          payment.getCreatedAt()
      );
    }
  }
}
