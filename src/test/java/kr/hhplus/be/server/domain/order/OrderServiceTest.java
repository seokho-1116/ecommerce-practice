package kr.hhplus.be.server.domain.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.Optional;
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

  @InjectMocks
  private OrderService orderService;

  @DisplayName("주문 결제 시 주문이 결제 상태로 변경되어야 한다")
  @Test
  void payOrderTest() {
    // given
    long orderId = 1L;
    given(orderRepository.findByIdAndStatus(anyLong(), any())).willReturn(Optional.of(
        Order.builder()
            .id(orderId)
            .status(OrderStatus.CREATED)
            .build()
    ));

    // when
    orderService.pay(orderId);

    // then
    verify(orderRepository, atLeastOnce()).save(any(Order.class));
  }

  @DisplayName("주문 결제 시 주문이 null이면 예외가 발생해야 한다")
  @Test
  void payOrderWhenOrderIsNullTest() {
    // given
    Long orderId = null;

    // when
    // then
    assertThatThrownBy(() -> orderService.pay(orderId))
        .isInstanceOf(OrderBusinessException.class);
  }

  @DisplayName("주문 조회 시 주문 ID가 null이면 예외가 발생해야 한다")
  @Test
  void findNotPaidOrderByIdWhenOrderIdIsNullTest() {
    // given
    Long orderId = null;

    // when
    // then
    assertThatThrownBy(() -> orderService.findNotPaidOrderById(orderId))
        .isInstanceOf(OrderBusinessException.class);
  }
}