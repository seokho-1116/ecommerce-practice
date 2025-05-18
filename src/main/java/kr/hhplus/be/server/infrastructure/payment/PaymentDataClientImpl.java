package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentDataClient;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataClientImpl implements PaymentDataClient {

  @Override
  public void publish(PaymentSuccessPayload event) {
    // async
  }
}