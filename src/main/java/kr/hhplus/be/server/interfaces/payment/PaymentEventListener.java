package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
import kr.hhplus.be.server.domain.payment.PaymentDataClient;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

  private final PaymentService paymentService;
  private final PaymentDataClient paymentDataClient;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleOrderSuccessEvent(OrderEvent.OrderSuccessEvent event) {
    PaymentSuccessCommand command = PaymentSuccessCommand.of(event.orderId(), event.orderId(),
        event.finalPrice());
    paymentService.pay(command);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handlePaymentSuccessEvent(PaymentSuccessEvent event) {
    PaymentSuccessPayload payload = PaymentSuccessPayload.from(event);
    paymentDataClient.publish(payload);
  }
}