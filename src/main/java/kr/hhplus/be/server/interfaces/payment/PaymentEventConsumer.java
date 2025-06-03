package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.payment.PaymentDataClient;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

  private final PaymentDataClient paymentDataClient;

  @KafkaListener(topics = "payment.v1.success", concurrency = "1")
  public void handlePaymentSuccessEvent(PaymentSuccessEvent event, Acknowledgment ack) {
    PaymentSuccessPayload payload = PaymentSuccessPayload.from(event);
    paymentDataClient.publish(payload);
    ack.acknowledge();
  }
}
