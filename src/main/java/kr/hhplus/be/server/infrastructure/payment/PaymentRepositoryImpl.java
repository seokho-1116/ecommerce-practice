package kr.hhplus.be.server.infrastructure.payment;

import java.util.Optional;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

  private final PaymentJpaRepository paymentJpaRepository;

  @Override
  public Payment save(Payment payment) {
    return paymentJpaRepository.save(payment);
  }

  @Override
  public Optional<Payment> findByOrderIdAndUserId(Long orderId, Long userId) {
    return paymentJpaRepository.findByOrderIdAndUserId(orderId, userId);
  }
}
