package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.payment.PaymentDataClient;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

  private final PaymentDataClient paymentDataClient;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
    PaymentSuccessPayload payload = PaymentSuccessPayload.from(event);
    paymentDataClient.publish(payload);
  }
}
