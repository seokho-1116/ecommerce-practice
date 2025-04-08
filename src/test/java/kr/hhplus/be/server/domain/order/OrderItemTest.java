package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {

  @DisplayName("주문 상품이 생성되면 최종 금액이 null 이 아니여야 한다")
  @Test
  void createOrderItemTest() {
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
    OrderProductPair orderProductPair = new OrderProductPair(null, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty().allMatch(orderItem -> orderItem.getFinalPrice() != null);
  }

  @DisplayName("주문 상품의 최종 금액이 0보다 작으면 0으로 설정된다")
  @Test
  void createOrderItemWithNegativeFinalPriceTest() {
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

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .id(1L)
        .couponType(CouponType.FIXED)
        .discountAmount(10000L)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .build();

    OrderProductPair orderProductPair = new OrderProductPair(userCoupon, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty().allMatch(orderItem -> orderItem.getFinalPrice() == 0L);
  }

  @DisplayName("주문 상품의 쿠폰이 null이면 쿠폰 할인 금액이 0으로 설정된다")
  @Test
  void createOrderItemWithNullCouponTest() {
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

    OrderProductPair orderProductPair = new OrderProductPair(null, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty().allMatch(orderItem -> orderItem.getDiscountPrice() == 0L);
  }

  @DisplayName("주문 상품의 전체 금액은 주문 상품의 기본 금액과 추가 금액의 합이다")
  @Test
  void createOrderItemWithTotalPriceTest() {
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

    OrderProductPair orderProductPair = new OrderProductPair(null, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty()
        .allMatch(orderItem -> orderItem.getTotalPrice()
            == product.getBasePrice() + productOption.getAdditionalPrice());
  }

  @DisplayName("주문 상품의 최종 금액은 전체 금액에서 쿠폰 할인 금액을 뺀 값이다")
  @Test
  void createOrderItemWithFinalPriceTest() {
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

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .id(1L)
        .couponType(CouponType.FIXED)
        .discountAmount(100L)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .build();

    OrderProductPair orderProductPair = new OrderProductPair(userCoupon, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    assertThat(orderItems).isNotEmpty()
        .allMatch(orderItem -> orderItem.getFinalPrice()
            == orderItem.getTotalPrice() - orderItem.getDiscountPrice());
  }

  @DisplayName("주문 상품에서 쿠폰을 사용하면 쿠폰 할인 금액에 따라 할인 금액이 설정된다")
  @Test
  void createOrderItemWithCouponTest() {
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

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .id(1L)
        .couponType(CouponType.PERCENTAGE)
        .discountRate(0.1)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .build();

    OrderProductPair orderProductPair = new OrderProductPair(userCoupon, productOption, 5L);

    // when
    List<OrderItem> orderItems = OrderItem.create(orderProductPair);

    // then
    long discountPrice = (long) ((product.getBasePrice() + productOption.getAdditionalPrice())
        * 0.1);
    assertThat(orderItems).isNotEmpty()
        .allMatch(orderItem -> orderItem.getDiscountPrice() == discountPrice);
  }
}