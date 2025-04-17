package kr.hhplus.be.server.domain.order;

import java.util.Optional;

public interface OrderRepository {

  Order save(Order order);

  Optional<Order> findByIdAndStatus(Long orderId, OrderStatus orderStatus);
}
