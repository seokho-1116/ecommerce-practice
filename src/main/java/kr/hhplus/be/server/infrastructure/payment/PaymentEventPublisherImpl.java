package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisherImpl implements PaymentEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void success(PaymentSuccessEvent event) {
    kafkaTemplate.send("payment.v1.success", String.valueOf(event.orderId()), event);
  }
}