package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import java.util.List;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderNotFoundException;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;

  @Transactional
  public OrderInfo createOrder(OrderCommand orderCommand) {
    List<OrderItem> orderItems = orderCommand.productAmountPairs().stream()
        .map(OrderItem::create)
        .toList();

    Order order = Order.newOrder(orderCommand.user(), orderItems, orderCommand.userCoupon());

    Order savedOrder = orderRepository.save(order);

    return OrderInfo.from(savedOrder);
  }

  public Order findNotPaidOrderById(Long orderId) {
    if (orderId == null) {
      throw new OrderBusinessException("주문 ID는 null일 수 없습니다.");
    }

    return orderRepository.findByIdAndStatus(orderId, OrderStatus.CREATED)
        .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
  }

  @Transactional
  public void pay(Long orderId) {
    if (orderId == null) {
      throw new OrderBusinessException("결제 상태로 변경할 주문이 없습니다.");
    }

    Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.CREATED)
        .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));

    order.pay();
    orderRepository.save(order);
  }
}
