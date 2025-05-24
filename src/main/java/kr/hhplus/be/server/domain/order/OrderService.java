package kr.hhplus.be.server.domain.order;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderNotFoundException;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderEventPublisher orderEventPublisher;

  @Transactional
  public OrderInfo createOrder(OrderCommand orderCommand) {
    List<OrderItem> orderItems = orderCommand.productAmountPairs().stream()
        .map(OrderItem::create)
        .toList();

    Order order = Order.newOrder(orderCommand.user(), orderItems, orderCommand.userCoupon());

    Order savedOrder = orderRepository.save(order);

    return OrderInfo.from(savedOrder);
  }

  @Transactional
  public OrderInfo payOrder(OrderPaymentCommand orderPaymentCommand) {
    Order order = orderRepository.findByIdAndStatus(orderPaymentCommand.orderId(),
            OrderStatus.CREATED)
        .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다."));

    order.pay();
    Order savedOrder = orderRepository.save(order);

    Map<Long, Long> productOptionIdToAmountMap = savedOrder.getOrderItems().stream()
        .collect(groupingBy(OrderItem::getProductOptionId, counting()));
    OrderPaymentSuccessEvent orderPaymentSuccessEvent = OrderPaymentSuccessEvent.from(savedOrder,
        productOptionIdToAmountMap);
    orderEventPublisher.paySuccess(orderPaymentSuccessEvent);

    return OrderInfo.from(savedOrder);
  }
}
