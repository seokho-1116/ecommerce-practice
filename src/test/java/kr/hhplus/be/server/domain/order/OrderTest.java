package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

  @DisplayName("주문 상품의 전체 금액의 합이 주문 금액과 같아야 한다.")
  @Test
  void newOrderWhenOrderItemsTotalPrice() {
    // given
    OrderItem orderItem1 = getOrderItemWithRandomPrice();

    OrderItem orderItem2 = getOrderItemWithRandomPrice();

    // when
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2));

    // then
    long totalPrice = orderItem1.getTotalPrice() + orderItem2.getTotalPrice();
    Assertions.assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
  }

  private static OrderItem getOrderItemWithRandomPrice() {
    return OrderItem.builder()
        .basePrice(ThreadLocalRandom.current().nextLong(10000, 100000))
        .additionalPrice(ThreadLocalRandom.current().nextLong(0, 10000))
        .totalPrice(ThreadLocalRandom.current().nextLong(10000, 100000))
        .discountPrice(ThreadLocalRandom.current().nextLong(0, 10000))
        .finalPrice(ThreadLocalRandom.current().nextLong(10000, 100000))
        .build();
  }

  @DisplayName("주문 상품의 할인 금액의 합이 주문 할인 금액과 같아야 한다.")
  @Test
  void newOrderWhenOrderItemsDiscountPrice() {
    // given
    OrderItem orderItem1 = getOrderItemWithRandomPrice();

    OrderItem orderItem2 = getOrderItemWithRandomPrice();

    // when
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2));

    // then
    long discountPrice = orderItem1.getDiscountPrice() + orderItem2.getDiscountPrice();
    Assertions.assertThat(order.getDiscountPrice()).isEqualTo(discountPrice);
  }

  @DisplayName("주문 상품의 최종 금액의 합이 주문 최종 금액과 같아야 한다.")
  @Test
  void newOrderWhenOrderItemsFinalPrice() {
    // given
    OrderItem orderItem1 = getOrderItemWithRandomPrice();

    OrderItem orderItem2 = getOrderItemWithRandomPrice();

    // when
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2));

    // then
    long finalPrice = orderItem1.getFinalPrice() + orderItem2.getFinalPrice();
    Assertions.assertThat(order.getFinalPrice()).isEqualTo(finalPrice);
  }
}