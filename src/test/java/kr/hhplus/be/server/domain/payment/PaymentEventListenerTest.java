package kr.hhplus.be.server.domain.payment;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentEventListenerTest {

  @Mock
  private PaymentDataClient paymentDataClient;

  @InjectMocks
  private PaymentEventListener paymentEventListener;

  @DisplayName("결제 성공 이벤트를 리슨한다")
  @Test
  void listenPaymentSuccessEvent() {
    // given
    PaymentEvent.PaymentSuccessEvent event = new PaymentEvent.PaymentSuccessEvent(1L, 1L);

    // when
    paymentEventListener.handlePaymentSuccessEvent(event);

    // then
    verify(paymentDataClient, atLeastOnce()).publish(PaymentSuccessPayload.from(event));
  }
}