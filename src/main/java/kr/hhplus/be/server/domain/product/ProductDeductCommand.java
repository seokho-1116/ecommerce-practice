package kr.hhplus.be.server.domain.product;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.util.List;
import java.util.Map;
import kr.hhplus.be.server.domain.order.OrderDto.OrderItemInfo;
import kr.hhplus.be.server.domain.order.OrderItem;

public record ProductDeductCommand(
    Map<Long, Long> productOptionIdToAmountMap
) {

  public static ProductDeductCommand from(List<OrderItemInfo> orderItems) {
    if (orderItems == null || orderItems.isEmpty()) {
      return new ProductDeductCommand(Map.of());
    }

    Map<Long, Long> productOptionIdToAmountMap = orderItems.stream()
        .collect(groupingBy(OrderItemInfo::productOptionId, counting()));

    return new ProductDeductCommand(productOptionIdToAmountMap);
  }

  public List<Long> productOptionIds() {
    return productOptionIdToAmountMap.keySet()
        .stream()
        .toList();
  }

  public Long getAmount(Long productOptionId) {
    if (productOptionId == null) {
      return null;
    }

    return productOptionIdToAmountMap.get(productOptionId);
  }

  public boolean isEmpty() {
    return productOptionIdToAmountMap == null || productOptionIdToAmountMap.isEmpty();
  }
}
