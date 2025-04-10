package kr.hhplus.be.server.domain.order;

import java.util.List;
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
}
