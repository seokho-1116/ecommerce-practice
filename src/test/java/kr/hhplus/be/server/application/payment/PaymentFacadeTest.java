package kr.hhplus.be.server.application.payment;

import static org.assertj.core.api.Assertions.assertThat;

import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "classpath:db/payment_test_case.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentFacadeTest {

  @Autowired
  private PaymentFacade paymentFacade;

  @DisplayName("성공한 주문을 결제하면 결제 상태가 성공으로 변경되어야 한다")
  @Test
  void test() {
    // given
    Long orderId = 1L;
    Long userId = 1L;
    PaymentCommand paymentCommand = new PaymentCommand(orderId, userId);

    // when
    PaymentResult paymentResult = paymentFacade.payOrder(paymentCommand);

    // then
    assertThat(paymentResult.order().getStatus()).isNotNull().isEqualTo(OrderStatus.PAID);
    assertThat(paymentResult.remainingPoint()).isGreaterThanOrEqualTo(0L);
  }
}