package kr.hhplus.be.server.application.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.Item;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.ProductIdItemPair;
import kr.hhplus.be.server.domain.order.OrderEvent.UseCouponEvent;
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
class OrderFacadeIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private OrderFacade orderFacade;

  @Autowired
  private ApplicationEvents events;

  private User user;
  private UserCoupon userCoupon;
  private Product product;
  private List<ProductOption> productOptions;

  @BeforeEach
  void setup() {
    // user
    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    // coupon
    Coupon coupon = couponTestDataGenerator.validCoupon();
    testHelpRepository.save(coupon);

    // user coupon
    userCoupon = couponTestDataGenerator.notUsedUserCoupon(user, coupon);
    testHelpRepository.save(userCoupon);

    // product
    product = productTestDataGenerator.product();
    testHelpRepository.save(product);

    // product option
    productOptions = productTestDataGenerator.productOptions(5);

    // product inventory
    productOptions.forEach(productOption -> {
      productOption.setupProduct(product);
      testHelpRepository.save(productOption);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      productInventory.setupProductOption(productOption);
      testHelpRepository.save(productInventory);
    });
  }

  @DisplayName("주문 생성 시 쿠폰이 존재하면 쿠폰 사용 이벤트가 발생한다")
  @Test
  void createOrderWithCoupon() {
    // given
    long userId = user.getId();
    long userCouponId = userCoupon.getId();
    List<ProductIdItemPair> productIdItemPairs = List.of(
        new ProductIdItemPair(product.getId(), new Item(productOptions.get(0).getId(), 1L))
    );
    OrderCreateCommand command = new OrderCreateCommand(
        userId,
        userCouponId,
        productIdItemPairs
    );

    // when
    orderFacade.createOrder(command);

    // then
    List<UseCouponEvent> result = events.stream(UseCouponEvent.class)
        .toList();
    assertThat(result).hasSize(1);
    assertThat(result)
        .extracting(UseCouponEvent::userCouponId)
        .containsExactly(userCouponId);
  }
}