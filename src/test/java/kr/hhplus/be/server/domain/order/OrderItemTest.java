package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderItemIllegalStateException;
import kr.hhplus.be.server.domain.order.OrderCommand.ProductAmountPair;
import kr.hhplus.be.server.domain.order.OrderItem.OrderItemBuilder;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {

  @DisplayName("주문 상품은 수량만큼 생성된다")
  @Test
  void createAllOrderItemTestWithAmount() {
    // given
    Product product = Product.builder()
        .id(1L)
        .name("test")
        .basePrice(1000L)
        .description("test")
        .build();

    ProductOption productOption = ProductOption.builder()
        .id(1L)
        .name("test")
        .additionalPrice(100L)
        .description("test")
        .product(product)
        .build();

    long amount = 5L;
    ProductAmountPair productAmountPair = new ProductAmountPair(product, productOption, amount);

    // when
    List<OrderItem> orderItems = OrderItem.createAll(productAmountPair);

    // then
    assertThat(orderItems).hasSize((int) amount);
  }

  @DisplayName("주문 상품의 전체 금액은 주문 상품의 기본 금액과 추가 금액의 합이다")
  @Test
  void createAllOrderItemWithTotalPriceTest() {
    // given
    Product product = Product.builder()
        .id(1L)
        .name("test")
        .basePrice(1000L)
        .description("test")
        .build();

    ProductOption productOption = ProductOption.builder()
        .id(1L)
        .name("test")
        .additionalPrice(100L)
        .description("test")
        .product(product)
        .build();

    ProductAmountPair orderProductPair = new ProductAmountPair(product, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.createAll(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty()
        .allMatch(orderItem -> orderItem.getTotalPrice()
            == product.getBasePrice() + productOption.getAdditionalPrice());
  }

  @DisplayName("주문 상품의 기본 금액은 0 보다 작으면 주문 상태 예외가 발생한다")
  @Test
  void createOrderItemWithBasePriceTest() {
    // given
    OrderItemBuilder orderItemBuilder = OrderItem.builder()
        .basePrice(-100L)
        .additionalPrice(100L)
        .totalPrice(0L);

    // when & then
    assertThatThrownBy(orderItemBuilder::build)
        .isInstanceOf(OrderItemIllegalStateException.class);
  }

  @DisplayName("주문 상품의 추가 금액은 0 보다 작으면 주문 상태 예외가 발생한다")
  @Test
  void createOrderItemWithAdditionalPriceTest() {
    // given
    OrderItemBuilder orderItemBuilder = OrderItem.builder()
        .basePrice(100L)
        .additionalPrice(-100L)
        .totalPrice(0L);

    // when
    // then
    assertThatThrownBy(orderItemBuilder::build)
        .isInstanceOf(OrderItemIllegalStateException.class);
  }

  @DisplayName("주문 상품의 총 금액은 0 보다 작으면 주문 상태 예외가 발생한다")
  @Test
  void createOrderItemWithTotalPriceTest() {
    // given
    OrderItemBuilder orderItemBuilder = OrderItem.builder()
        .basePrice(100L)
        .additionalPrice(100L)
        .totalPrice(-1L);

    // when
    // then
    assertThatThrownBy(orderItemBuilder::build)
        .isInstanceOf(OrderItemIllegalStateException.class);
  }
}