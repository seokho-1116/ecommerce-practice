package kr.hhplus.be.server.domain.payment;

import jakarta.transaction.Transactional;
import java.util.Optional;
import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentIllegalStateException;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;

  @Transactional
  public PaymentInfo pay(PaymentSuccessCommand command) {
    Optional<Payment> existingPayment = paymentRepository.findByOrderIdAndUserId(command.orderId(), command.userId());
    if (existingPayment.isPresent()) {
      throw new PaymentIllegalStateException("이미 결제된 주문입니다.");
    }

    Payment payment = Payment.success(command);

    Payment saved = paymentRepository.save(payment);

    return PaymentInfo.from(saved);
  }
}
