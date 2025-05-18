package kr.hhplus.be.server.domain.payment;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PaymentEventPublisherTest {

  @Mock
  private ApplicationEventPublisher publisher;

  @InjectMocks
  private PaymentEventPublisher paymentEventPublisher;

  @DisplayName("결제 성공 이벤트를 발행한다")
  @Test
  void publishPaymentSuccessEvent() {
    // given
    PaymentSuccessEvent event = new PaymentSuccessEvent(1L, 1L);

    // when
    paymentEventPublisher.success(event);

    // then
    verify(publisher, atLeastOnce()).publishEvent(event);
  }
}