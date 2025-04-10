package kr.hhplus.be.server.interfaces.coupon;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CouponFacade couponFacade;

  @MockitoBean
  private CouponService couponService;

  @Autowired
  private ObjectMapper objectMapper;

  @DisplayName("쿠폰 목록 조회 API 테스트")
  @Test
  void findAllCouponsTest() throws Exception {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon1 = Coupon.builder()
        .id(1L)
        .name("쿠폰1")
        .discountAmount(1000L)
        .couponType(CouponType.FIXED)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    Coupon coupon2 = Coupon.builder()
        .id(2L)
        .name("쿠폰2")
        .discountAmount(2000L)
        .couponType(CouponType.FIXED)
        .fromTs(now.minusDays(1))
        .toTs(now.plusDays(1))
        .build();

    when(couponService.findAllCoupons()).thenReturn(List.of(coupon1, coupon2));

    // when
    // then
    mockMvc.perform(get("/api/v1/coupons"))
        .andExpect(status().isOk());
  }

  @DisplayName("쿠폰 발급 API 테스트")
  @Test
  void issueCouponTest() throws Exception {
    // given
    Long userId = 1L;
    Long couponId = 1L;

    Coupon coupon = Coupon.builder()
        .id(couponId)
        .name("쿠폰")
        .discountAmount(1000L)
        .couponType(CouponType.FIXED)
        .fromTs(LocalDateTime.now().minusDays(1))
        .toTs(LocalDateTime.now().plusDays(1))
        .build();

    User user = User.builder()
        .id(userId)
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .user(user)
        .build();

    when(couponService.issue(user, couponId)).thenReturn(userCoupon);

    CouponIssueRequest request = new CouponIssueRequest(userId, couponId);
    when(couponFacade.issue(userId, couponId)).thenReturn(userCoupon);

    // when
    // then
    mockMvc.perform(post("/api/v1/coupons/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}