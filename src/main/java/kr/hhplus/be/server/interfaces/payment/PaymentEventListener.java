package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.domain.order.OrderEvent;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
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

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handlePaymentEvent(OrderEvent.OrderSuccessEvent event) {
    PaymentSuccessCommand command = PaymentSuccessCommand.of(event.orderId(), event.orderId(),
        event.finalPrice());
    paymentService.pay(command);
  }
}