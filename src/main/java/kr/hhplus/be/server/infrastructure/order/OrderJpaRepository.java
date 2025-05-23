package kr.hhplus.be.server.infrastructure.order;

import java.util.Optional;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

  @Query("SELECT o FROM Order o JOIN FETCH o.orderItems oi WHERE o.id = :id AND o.status = :orderStatus")
  Optional<Order> findFetchedById(Long id, OrderStatus orderStatus);
}
