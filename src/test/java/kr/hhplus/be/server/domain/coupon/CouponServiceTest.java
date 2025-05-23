package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

  @Mock
  private CouponRepository couponRepository;

  @InjectMocks
  private CouponService couponService;

  @DisplayName("쿠폰 사용 시 쿠폰 사용이 호출되어야 한다")
  @Test
  void useCouponTest() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Coupon coupon = Coupon.builder()
        .couponType(CouponType.FIXED)
        .discountAmount(1000L)
        .from(now.minusMonths(1))
        .to(now.plusMonths(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .isUsed(false)
        .build();
    when(couponRepository.findUserCouponByUserCouponId(anyLong())).thenReturn(
        Optional.of(userCoupon));

    // when
    couponService.use(userCoupon.getId());

    // then
    verify(couponRepository, atLeastOnce()).saveUserCoupon(userCoupon);
    assertThat(userCoupon.getIsUsed()).isTrue();
  }
}