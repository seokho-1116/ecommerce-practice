package kr.hhplus.be.server.domain.order;

import static org.instancio.Select.field;

import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class OrderTestDataGenerator {

  public Order order(Long userId, OrderStatus orderStatus) {
    return Instancio.of(Order.class)
        .ignore(field(Order::getId))
        .set(field(Order::getUserId), userId)
        .set(field(Order::getIsActive), true)
        .set(field(Order::getVersion), 1L)
        .ignore(field(Order::getCreatedAt))
        .ignore(field(Order::getUpdatedAt))
        .set(field(Order::getStatus), orderStatus)
        .create();
  }

  public OrderItem orderItem(Order order, Long productOptionId) {
    return Instancio.of(OrderItem.class)
        .set(field(OrderItem::getIsActive), true)
        .ignore(field(OrderItem::getId))
        .ignore(field(OrderItem::getCreatedAt))
        .ignore(field(OrderItem::getUpdatedAt))
        .set(field(OrderItem::getOrder), order)
        .set(field(OrderItem::getProductOptionId), productOptionId)
        .create();
  }
}
