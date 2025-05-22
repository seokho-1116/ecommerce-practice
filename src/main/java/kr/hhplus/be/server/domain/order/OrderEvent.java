package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;

public class OrderEvent {

  private OrderEvent() {
  }

  public record OrderSuccessEvent(
      Long orderId,
      Long userId
  ) {

    public static OrderSuccessEvent from(OrderInfo orderResult) {
      return new OrderSuccessEvent(
          orderResult.id(),
          orderResult.userId()
      );
    }
  }
}
