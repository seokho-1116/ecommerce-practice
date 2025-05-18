package kr.hhplus.be.server.infrastructure.payment;

import java.util.Optional;
import kr.hhplus.be.server.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

  Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);
}
