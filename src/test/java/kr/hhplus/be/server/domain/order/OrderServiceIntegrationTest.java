package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponTestDataGenerator;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.product.ProductTestDataGenerator;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private OrderService orderService;

  @Autowired
  private OrderTestDataGenerator orderTestDataGenerator;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  @Autowired
  private ProductTestDataGenerator productTestDataGenerator;

  @Autowired
  private CouponTestDataGenerator couponTestDataGenerator;

  private Order order;

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
    order = orderTestDataGenerator.order(user.getId(), OrderStatus.CREATED);
    testHelpRepository.save(order);

    // product inventory
    productOptions.forEach(productOption -> {
      productOption.setupProduct(product);
      testHelpRepository.save(productOption);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      productInventory.setupProductOption(productOption);
      testHelpRepository.save(productInventory);

      OrderItem orderItem = orderTestDataGenerator.orderItem(order, productOption.getId());
      orderItem.setupOrder(order);
      testHelpRepository.save(orderItem);
    });
  }

  @DisplayName("주문 결제 시 주문이 결제 상태로 변경되어야 한다")
  @Test
  void createOrderTest() {
    // given
    Order notPaidOrder = orderService.findNotPaidOrderById(order.getId());

    // when
    orderService.pay(notPaidOrder);

    // then
    assertThat(notPaidOrder.getStatus()).isEqualTo(OrderStatus.PAID);
  }

  @DisplayName("동시에 주문 결제를 요청하면 하나만 결제된다")
  @Test
  void concurrentPayOrderTest() throws InterruptedException {
    // given
    Order notPaidOrder = orderService.findNotPaidOrderById(order.getId());
    int threadCount = 15;
    CountDownLatch latch = new CountDownLatch(threadCount);

    // when
    AtomicLong successCount = new AtomicLong(0);
    AtomicLong failedCount = new AtomicLong(0);
    for (int i = 0; i < threadCount; i++) {
      new Thread(() -> {
        try {
          orderService.pay(notPaidOrder);
          successCount.incrementAndGet();
        } catch (Exception e) {
          failedCount.incrementAndGet();
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    assertThat(successCount.get()).isEqualTo(1L);
  }
}
