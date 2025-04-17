package kr.hhplus.be.server.domain.payment;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentEventIllegalStateException;

public record PaymentSuccessEvent(
    Long orderId,
    Long userId,
    Long finalPrice,
    List<OrderItemSummary> orderItemSummaries
) {

  public static PaymentSuccessEvent from(Order order) {
    if (order == null) {
      throw new PaymentEventIllegalStateException("결제 성공 이벤트를 생성할 주문이 없습니다.");
    }

    Map<Long, Long> orderItemAmountMap = order.getOrderItems()
        .stream()
        .collect(groupingBy(OrderItem::getProductOptionId, counting()));

    List<OrderItemSummary> orderItemSummaries = order.getOrderItems()
        .stream()
        .map(orderItem -> {
          Long amount = orderItemAmountMap.get(orderItem.getProductOptionId());

          return new OrderItemSummary(
              orderItem.getId(),
              orderItem.getProductName(),
              orderItem.getProductOptionName(),
              orderItem.getProductDescription(),
              orderItem.getProductOptionDescription(),
              amount
          );
        })
        .toList();

    return new PaymentSuccessEvent(
        order.getId(),
        order.getUserId(),
        order.getFinalPrice(),
        orderItemSummaries
    );
  }

  public record OrderItemSummary(
      Long orderItemId,
      String productName,
      String productOptionName,
      String productDescription,
      String productOptionDescription,
      Long amount
  ) {

  }
}
