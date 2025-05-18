package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void success(PaymentSuccessEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}
