package kr.hhplus.be.server.domain.order;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderItemIllegalStateException;

public record OrderCommand(
    Long userId,
    List<OrderItemOptionCommand> orderItemOptionCommands
) {

  public List<Long> userCouponIds() {
    return orderItemOptionCommands.stream()
        .map(OrderItemOptionCommand::userCouponId)
        .toList();
  }

  public List<Long> productOptionIds() {
    return orderItemOptionCommands.stream()
        .map(OrderItemOptionCommand::productOptionId)
        .toList();
  }

  public record OrderItemOptionCommand(
      Long productId,
      Long productOptionId,
      Long userCouponId,
      Long amount
  ) {

    public OrderItemOptionCommand {
      if (amount == null) {
        throw new OrderItemIllegalStateException("상품에 대한 주문 수량은 필수입니다.");
      }
    }
  }
}
