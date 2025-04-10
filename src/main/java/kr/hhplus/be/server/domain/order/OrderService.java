package kr.hhplus.be.server.domain.order;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

  private OrderRepository orderRepository;

  public Order createOrder(OrderCommand orderCommand) {
    List<OrderItem> orderItems = orderCommand.productAmountPairs().stream()
        .flatMap(productAmountPair -> OrderItem.createAll(productAmountPair)
            .stream())
        .toList();

    Order order = Order.newOrder(orderCommand.user(), orderItems, orderCommand.userCoupon());

    return orderRepository.save(order);
  }

  public Order findById(Long orderId) {
    if (orderId == null) {
      throw new OrderBusinessException("주문 ID는 null일 수 없습니다.");
    }

    return orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));
  }

  public void pay(Order order) {
    if (order == null) {
      throw new OrderBusinessException("결제 상태로 변경할 주문이 없습니다.");
    }

    order.pay();
    orderRepository.save(order);
  }
}
