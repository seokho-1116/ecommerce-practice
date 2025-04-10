package kr.hhplus.be.server.interfaces.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.order.OrderRequest.AmountProductOptionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private OrderFacade orderFacade;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("주문 생성 API 테스트")
  @Test
  void createOrderTest() throws Exception {
    // given
    OrderRequest request = new OrderRequest(
        1L,
        1L,
        List.of(
            new AmountProductOptionRequest(1L, 100L, 1L)
        )
    );

    User user = User.builder()
        .id(1L)
        .build();

    Order order = Order.builder()
        .id(1L)
        .status(OrderStatus.CREATED)
        .user(user)
        .build();

    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .id(1L)
        .name("쿠폰")
        .discountAmount(1000L)
        .couponType(CouponType.FIXED)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .user(user)
        .coupon(coupon)
        .build();

    when(orderFacade.createOrder(any())).thenReturn(OrderResult.of(order, userCoupon));

    // when
    // then
    mockMvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}