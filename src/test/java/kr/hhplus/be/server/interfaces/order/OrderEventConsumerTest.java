package kr.hhplus.be.server.interfaces.order;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import kr.hhplus.be.server.domain.payment.PaymentDataClient;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentSuccessPayload;
import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import kr.hhplus.be.server.interfaces.payment.PaymentEventConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

  @Mock
  private PaymentDataClient paymentDataClient;

  @InjectMocks
  private PaymentEventConsumer paymentEventConsumer;

  @DisplayName("결제 성공 이벤트를 리슨한다")
  @Test
  void listenPaymentSuccessEvent() {
    // given
    PaymentSuccessEvent event = new PaymentSuccessEvent(1L, 1L);

    // when
    paymentEventConsumer.handlePaymentSuccessEvent(event, () -> {
    });

    // then
    verify(paymentDataClient, atLeastOnce()).publish(PaymentSuccessPayload.from(event));
  }
}