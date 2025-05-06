package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

  @Override
  public void publish(PaymentSuccessEvent event) {
    // async
  }
}