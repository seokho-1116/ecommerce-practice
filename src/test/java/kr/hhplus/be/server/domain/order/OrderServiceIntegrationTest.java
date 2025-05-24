package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@RecordApplicationEvents
class OrderServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private OrderService orderService;

  @Autowired
  private ApplicationEvents events;

  private Order notPaidOrder;

  @BeforeEach
  void setup() {
    // user
    User user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    // coupon
    Coupon coupon = couponTestDataGenerator.coupon();
    testHelpRepository.save(coupon);

    // user coupon
    UserCoupon userCoupon = couponTestDataGenerator.userCoupon(user, coupon);
    testHelpRepository.save(userCoupon);

    // product
    Product product = productTestDataGenerator.product();
    testHelpRepository.save(product);

    // product option
    List<ProductOption> productOptions = productTestDataGenerator.productOptions(5);

    // order
    notPaidOrder = orderTestDataGenerator.order(user.getId(), OrderStatus.CREATED);
    testHelpRepository.save(notPaidOrder);

    // product inventory
    productOptions.forEach(productOption -> {
      productOption.setupProduct(product);
      testHelpRepository.save(productOption);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      productInventory.setupProductOption(productOption);
      testHelpRepository.save(productInventory);

      OrderItem orderItem = orderTestDataGenerator.orderItem(notPaidOrder, productOption.getId());
      orderItem.setupOrder(notPaidOrder);
      testHelpRepository.save(orderItem);
    });
  }

  @DisplayName("주문 결제 시 주문이 결제 상태로 변경되어야 한다")
  @Test
  void payOrderTest() {
    // given
    long userId = notPaidOrder.getUserId();
    long orderId = notPaidOrder.getId();
    OrderPaymentCommand command = new OrderPaymentCommand(userId, orderId);

    // when
    OrderInfo orderInfo = orderService.payOrder(command);

    // then
    assertThat(orderInfo.status()).isEqualTo(OrderStatus.PAID);
  }

  @DisplayName("주문 결제 시 주문 결제 이벤트에는 주문 아이디가 포함되어야 한다")
  @Test
  void createOrderTestWithOrderId() {
    // given
    OrderPaymentCommand command = new OrderPaymentCommand(notPaidOrder.getUserId(), notPaidOrder.getId());

    // when
    orderService.payOrder(command);

    // then
    List<OrderPaymentSuccessEvent> result = events.stream(OrderPaymentSuccessEvent.class)
        .toList();
    assertThat(result).hasSize(1)
        .extracting(OrderPaymentSuccessEvent::orderId)
        .containsExactly(notPaidOrder.getId());
  }
}
