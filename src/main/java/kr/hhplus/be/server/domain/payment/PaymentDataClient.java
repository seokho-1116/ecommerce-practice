package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;

public interface PaymentDataClient {

  void publish(PaymentSuccessPayload event);
}
