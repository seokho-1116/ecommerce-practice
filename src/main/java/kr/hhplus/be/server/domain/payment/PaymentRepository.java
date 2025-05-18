package kr.hhplus.be.server.domain.payment;

import java.util.Optional;

public interface PaymentRepository {

  Payment save(Payment payment);

  Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId);
}
