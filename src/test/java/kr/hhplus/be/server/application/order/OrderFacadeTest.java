package kr.hhplus.be.server.application.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.Item;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.ProductIdItemPair;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "classpath:db/order_test_case.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderFacadeTest {

  @Autowired
  private OrderFacade orderFacade;

  @DisplayName("주문 생성 시 주문에 성공해야 한다")
  @Test
  void test() {
    // given
    Long userId = 1L;
    Long userCouponId = 1L;
    OrderCreateCommand orderCreateCommand = new OrderCreateCommand(userId, userCouponId, List.of(
        new ProductIdItemPair(1L, new Item(1L, 100L)),
        new ProductIdItemPair(2L, new Item(4L, 200L)),
        new ProductIdItemPair(3L, new Item(6L, 300L))
    ));

    // when
    OrderResult order = orderFacade.createOrder(orderCreateCommand);

    // then
    assertThat(order.order().getStatus()).isEqualTo(OrderStatus.CREATED);
  }
}