package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order.OrderBuilder;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderIllegalStateException;
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
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2), null);

    // then
    long totalPrice = orderItem1.getTotalPrice() + orderItem2.getTotalPrice();
    assertThat(order.getTotalPrice()).isEqualTo(totalPrice);
  }

  private static OrderItem getOrderItemWithRandomPrice() {
    return OrderItem.builder()
        .basePrice(ThreadLocalRandom.current().nextLong(10000, 100000))
        .additionalPrice(ThreadLocalRandom.current().nextLong(0, 10000))
        .totalPrice(ThreadLocalRandom.current().nextLong(10000, 100000))
        .build();
  }

  @DisplayName("주문 상품의 할인 금액의 합이 주문 할인 금액과 같아야 한다.")
  @Test
  void newOrderWhenOrderItemsDiscountPrice() {
    // given
    OrderItem orderItem1 = getOrderItemWithRandomPrice();

    OrderItem orderItem2 = getOrderItemWithRandomPrice();

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();
    UserCoupon userCoupon = UserCoupon.builder()
        .coupon(coupon)
        .isUsed(false)
        .build();

    // when
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2), userCoupon);

    // then
    assertThat(order.getDiscountPrice()).isEqualTo(coupon.getDiscountAmount());
  }

  @DisplayName("주문 상품의 최종 금액의 합이 주문 최종 금액과 같아야 한다.")
  @Test
  void newOrderWhenOrderItemsFinalPrice() {
    // given
    OrderItem orderItem1 = getOrderItemWithRandomPrice();

    OrderItem orderItem2 = getOrderItemWithRandomPrice();

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.PERCENTAGE)
        .discountRate(0.1)
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();
    UserCoupon userCoupon = UserCoupon.builder()
        .coupon(coupon)
        .isUsed(false)
        .build();

    // when
    Order order = Order.newOrder(null, List.of(orderItem1, orderItem2), userCoupon);

    // then
    long totalPrice = orderItem1.getTotalPrice() + orderItem2.getTotalPrice();
    long discountPrice = coupon.calculateDiscountPrice(totalPrice);
    assertThat(order.getFinalPrice()).isEqualTo(totalPrice - discountPrice);
  }

  @DisplayName("주문 상태가 null이면 주문 상태 예외가 발생한다")
  @Test
  void newOrderWhenStatusIsNull() {
    // given
    OrderBuilder orderBuilder = Order.builder();

    // when
    // then
    assertThatThrownBy(orderBuilder::build)
        .isInstanceOf(OrderIllegalStateException.class);
  }

  @DisplayName("주문 총 금액이 음수이면 주문 상태 예외가 발생한다")
  @Test
  void newOrderWhenTotalPriceIsNegative() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .totalPrice(-1L);

    // when
    // then
    assertThatThrownBy(orderBuilder::build)
        .isInstanceOf(OrderIllegalStateException.class);
  }

  @DisplayName("주문 총 금액이 0이면 주문의 총 금액이 0이다")
  @Test
  void newOrderWhenTotalPriceIsZero() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .status(OrderStatus.CREATED)
        .totalPrice(0L);

    // when
    Order order = orderBuilder.build();

    // then
    assertThat(order.getTotalPrice()).isZero();
  }

  @DisplayName("주문 할인 금액이 음수이면 주문 상태 예외가 발생한다")
  @Test
  void newOrderWhenDiscountPriceIsNegative() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .status(OrderStatus.CREATED)
        .discountPrice(-1L);

    // when
    // then
    assertThatThrownBy(orderBuilder::build)
        .isInstanceOf(OrderIllegalStateException.class);
  }

  @DisplayName("주문 할인 금액이 0이면 주문의 할인 금액이 0이다")
  @Test
  void newOrderWhenDiscountPriceIsZero() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .status(OrderStatus.CREATED)
        .discountPrice(0L);

    // when
    Order order = orderBuilder.build();

    // then
    assertThat(order.getDiscountPrice()).isZero();
  }

  @DisplayName("주문 최종 금액이 음수이면 주문 상태 예외가 발생한다")
  @Test
  void newOrderWhenFinalPriceIsNegative() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .status(OrderStatus.CREATED)
        .finalPrice(-1L);

    // when
    // then
    assertThatThrownBy(orderBuilder::build)
        .isInstanceOf(OrderIllegalStateException.class);
  }

  @DisplayName("주문 최종 금액이 0이면 주문의 최종 금액이 0이다")
  @Test
  void newOrderWhenFinalPriceIsZero() {
    // given
    OrderBuilder orderBuilder = Order.builder()
        .status(OrderStatus.CREATED)
        .finalPrice(0L);

    // when
    Order order = orderBuilder.build();

    // then
    assertThat(order.getFinalPrice()).isZero();
  }

  @DisplayName("주문이 이미 결제된 상태면 결제 상태로 변경할 수 없다")
  @Test
  void payWhenOrderIsPaid() {
    // given
    Order order = Order.builder()
        .status(OrderStatus.PAID)
        .build();

    // when
    // then
    assertThatThrownBy(order::pay)
        .isInstanceOf(OrderIllegalStateException.class);
  }
}