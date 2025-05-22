package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.Optional;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private OrderEventPublisher orderEventPublisher;

  @InjectMocks
  private OrderService orderService;

  @DisplayName("주문 결제 시 주문이 결제 상태로 변경되어야 한다")
  @Test
  void payOrderTest() {
    // given
    long userId = 1L;
    long orderId = 1L;
    OrderPaymentCommand command = new OrderPaymentCommand(userId, orderId);
    given(orderRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.of(
        Order.builder()
            .id(orderId)
            .status(OrderStatus.CREATED)
            .build()
    ));
    given(orderRepository.save(any(Order.class))).willReturn(
        Order.builder()
            .id(orderId)
            .status(OrderStatus.PAID)
            .build()
    );

    // when
    orderService.payOrder(command);

    // then
    verify(orderRepository, atLeastOnce()).save(any(Order.class));
  }

  @DisplayName("주문이 성공하면 주문 상태가 성공으로 변경되어야 한다")
  @Test
  void payOrderTestWithEvent() {
    // given
    long userId = 1L;
    long orderId = 1L;
    OrderPaymentCommand command = new OrderPaymentCommand(userId, orderId);
    Order order = Order.builder()
        .id(orderId)
        .status(OrderStatus.CREATED)
        .build();
    given(orderRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.of(order));
    given(orderRepository.save(any(Order.class))).willReturn(
        Order.builder()
            .id(orderId)
            .status(OrderStatus.PAID)
            .build()
    );

    // when
    OrderInfo orderInfo = orderService.payOrder(command);

    // then
    assertThat(orderInfo.status()).isEqualTo(OrderStatus.PAID);
  }

  @DisplayName("주문 결제 시 주문 이벤트는 정상적으로 발행되어야 한다")
  @Test
  void payOrderTestWithEventPublisher() {
    // given
    long userId = 1L;
    long orderId = 1L;
    OrderPaymentCommand command = new OrderPaymentCommand(userId, orderId);
    Order order = Order.builder()
        .id(orderId)
        .status(OrderStatus.CREATED)
        .build();
    given(orderRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.of(order));
    given(orderRepository.save(any(Order.class))).willReturn(
        Order.builder()
            .id(orderId)
            .status(OrderStatus.PAID)
            .build()
    );

    // when
    orderService.payOrder(command);

    // then
    verify(orderEventPublisher, atLeastOnce()).success(any(OrderSuccessEvent.class));
  }
}