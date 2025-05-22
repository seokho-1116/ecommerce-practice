package kr.hhplus.be.server.domain.order;

import java.util.Map;

public class OrderEvent {

  private OrderEvent() {
  }

  public record OrderSuccessEvent(
      Long orderId,
      Long userId,
      Long finalPrice,
      Map<Long, Long> productOptionIdAmountMap
  ) {

    public static OrderSuccessEvent from(Order order,
        Map<Long, Long> productOptionIdAmountMap) {
      return new OrderSuccessEvent(
          order.getId(),
          order.getUserId(),
          order.getFinalPrice(),
          productOptionIdAmountMap
      );
    }
  }
}
