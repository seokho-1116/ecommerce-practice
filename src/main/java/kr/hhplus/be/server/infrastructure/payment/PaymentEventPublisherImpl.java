package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void success(PaymentSuccessEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}