package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long amount;

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;
  private Long userId;
  private Long orderId;

  @Builder
  public Payment(Long id, Long amount, PaymentStatus status, Long userId, Long orderId) {
    this.id = id;
    this.amount = amount;
    this.status = status;
    this.userId = userId;
    this.orderId = orderId;
  }

  public static Payment success(PaymentSuccessCommand command) {
    return Payment.builder()
        .userId(command.userId())
        .orderId(command.orderId())
        .amount(command.amount())
        .status(PaymentStatus.SUCCESS)
        .build();
  }
}
