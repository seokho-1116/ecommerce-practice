package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.domain.payment.PaymentStatus;

public record OrderPaymentResponse(
    @Schema(description = "주문 ID")
    Long orderId,

    @Schema(description = "유저 ID")
    Long userId,

    @Schema(description = "결제 ID")
    Long paymentId,

    @Schema(description = "주문 총 가격")
    Long amount,

    @Schema(description = "잔여 포인트")
    Long remainingPoint,

    @Schema(description = "결제 상태")
    PaymentStatus status,

    @Schema(description = "결제 시점")
    LocalDateTime createdAt
) {

  public static OrderPaymentResponse from(PaymentResult paymentResult) {
    return new OrderPaymentResponse(
        paymentResult.paymentInfo().orderId(),
        paymentResult.paymentInfo().userId(),
        paymentResult.paymentInfo().id(),
        paymentResult.paymentInfo().amount(),
        paymentResult.remainingPoint(),
        paymentResult.paymentInfo().status(),
        paymentResult.paymentInfo().createdAt()
    );
  }
}
