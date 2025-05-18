package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import kr.hhplus.be.server.domain.payment.PaymentBusinessException.PaymentIllegalStateException;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private PaymentRepository paymentRepository;

  @InjectMocks
  private PaymentService paymentService;

  @DisplayName("기존 주문에 대한 결제가 존재하면 결제 상태 예외가 발생해야 한다")
  @Test
  void pay_ExistingPayment_ThrowsException() {
    // given
    Long orderId = 1L;
    Long userId = 1L;
    Long amount = 1000L;
    PaymentSuccessCommand command = new PaymentSuccessCommand(orderId, userId, amount);
    Payment payment = Payment.success(command);

    when(paymentRepository.findByOrderIdAndUserId(anyLong(), anyLong()))
        .thenReturn(Optional.of(payment));

    // when
    // then
    assertThatThrownBy(() -> paymentService.pay(command))
        .isInstanceOf(PaymentIllegalStateException.class);
  }
}