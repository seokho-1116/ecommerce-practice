package kr.hhplus.be.server.interfaces.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PaymentFacade paymentFacade;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("주문 결제 API 테스트")
  @Test
  void paymentOrderTest() throws Exception {
    // given
    long orderId = 1L;
    long userId = 1L;
    OrderPaymentRequest request = new OrderPaymentRequest(userId);

    User user = User.builder()
        .id(userId)
        .build();

    Order order = Order.builder()
        .id(orderId)
        .status(OrderStatus.CREATED)
        .user(user)
        .build();

    long remainingAmount = 1000L;
    when(paymentFacade.payment(any())).thenReturn(new PaymentResult(order, remainingAmount));

    // when
    // then
    mockMvc.perform(post("/api/v1/payments/{orderId}", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}