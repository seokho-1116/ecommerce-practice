package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;

public interface PaymentEventPublisher {

  void success(PaymentSuccessEvent event);
}