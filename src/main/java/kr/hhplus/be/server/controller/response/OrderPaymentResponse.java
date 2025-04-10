package kr.hhplus.be.server.controller.response;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.order.OrderStatus;

public record OrderPaymentResponse(
    Long orderId,
    Long userId,
    Long amount,
    OrderStatus status,
    Long remainingPoint,
    LocalDateTime createdAt
) {

}
