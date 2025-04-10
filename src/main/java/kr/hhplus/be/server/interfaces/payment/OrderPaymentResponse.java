package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.domain.order.OrderStatus;

public record OrderPaymentResponse(
    @Schema(description = "주문 ID")
    Long orderId,

    @Schema(description = "유저 ID")
    Long userId,

    @Schema(description = "주문 총 가격")
    Long amount,

    @Schema(description = "주문 상태")
    OrderStatus status,

    @Schema(description = "잔여 포인트")
    Long remainingPoint,

    @Schema(description = "주문 생성일")
    LocalDateTime createdAt
) {

  public static OrderPaymentResponse from(PaymentResult paymentResult) {
    return new OrderPaymentResponse(
        paymentResult.order().getId(),
        paymentResult.order().getUser().getId(),
        paymentResult.order().getTotalPrice(),
        paymentResult.order().getStatus(),
        paymentResult.remainingPoint(),
        LocalDateTime.now()
    );
  }
}
