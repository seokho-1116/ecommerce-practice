package kr.hhplus.be.server.domain.payment;

public interface PaymentDataClient {

  void publish(PaymentSuccessEvent event);
}
