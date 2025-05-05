package kr.hhplus.be.server.domain.payment;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderDto.OrderItemInfo;
import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentEventIllegalStateException;

public record PaymentSuccessEvent(
    Long orderId,
    Long userId,
    Long finalPrice,
    List<OrderItemSummary> orderItemSummaries
) {

  public static PaymentSuccessEvent from(OrderInfo order) {
    if (order == null) {
      throw new PaymentEventIllegalStateException("결제 성공 이벤트를 생성할 주문이 없습니다.");
    }

    Map<Long, Long> orderItemAmountMap = order.orderItems()
        .stream()
        .collect(groupingBy(OrderItemInfo::productOptionId, counting()));

    List<OrderItemSummary> orderItemSummaries = order.orderItems()
        .stream()
        .map(orderItem -> {
          Long amount = orderItemAmountMap.get(orderItem.productOptionId());

          return new OrderItemSummary(
              orderItem.id(),
              orderItem.productName(),
              orderItem.productOptionName(),
              orderItem.productDescription(),
              orderItem.productOptionDescription(),
              amount
          );
        })
        .toList();

    return new PaymentSuccessEvent(
        order.id(),
        order.userId(),
        order.finalPrice(),
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
