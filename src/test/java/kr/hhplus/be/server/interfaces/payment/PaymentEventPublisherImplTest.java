package kr.hhplus.be.server.interfaces.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import kr.hhplus.be.server.domain.payment.PaymentEvent.PaymentSuccessEvent;
import kr.hhplus.be.server.infrastructure.payment.PaymentEventPublisherImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class PaymentEventPublisherImplTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private PaymentEventPublisherImpl paymentEventPublisherImpl;

  @DisplayName("결제 성공 이벤트를 발행한다")
  @Test
  void publishPaymentSuccessEvent() {
    // given
    PaymentSuccessEvent event = new PaymentSuccessEvent(1L, 1L);

    // when
    paymentEventPublisherImpl.success(event);

    // then
    verify(kafkaTemplate, atLeastOnce()).send(any(), any(), any());
  }
}