package kr.hhplus.be.server.domain.order;

import java.util.List;

public class OrderDto {

  OrderDto() {
  }

  public record OrderInfo(
      Long id,
      Long totalPrice,
      Long discountPrice,
      Long finalPrice,
      OrderStatus status,
      Long userId,
      List<OrderItemInfo> orderItems
  ) {

    public static OrderInfo from(Order order) {
      List<OrderItemInfo> orderItems = order.getOrderItems().stream()
          .map(OrderItemInfo::from)
          .toList();
      return new OrderInfo(
          order.getId(),
          order.getTotalPrice(),
          order.getDiscountPrice(),
          order.getFinalPrice(),
          order.getStatus(),
          order.getUserId(),
          orderItems
      );
    }
  }

  public record OrderItemInfo(
      Long id,
      String productName,
      String productDescription,
      String productOptionName,
      String productOptionDescription,
      Long basePrice,
      Long additionalPrice,
      Long totalPrice,
      Long amount,
      Long productOptionId
  ) {

    public static OrderItemInfo from(OrderItem orderItem) {
      return new OrderItemInfo(
          orderItem.getId(),
          orderItem.getProductName(),
          orderItem.getProductDescription(),
          orderItem.getProductOptionName(),
          orderItem.getProductOptionDescription(),
          orderItem.getBasePrice(),
          orderItem.getAdditionalPrice(),
          orderItem.getTotalPrice(),
          orderItem.getAmount(),
          orderItem.getProductOptionId()
      );
    }
  }
}
