package kr.hhplus.be.server.domain.coupon;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
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
        .fromTs(now.minusMonths(1))
        .toTs(now.plusMonths(1))
        .build();

    UserCoupon userCoupon = UserCoupon.builder()
        .id(1L)
        .coupon(coupon)
        .isUsed(false)
        .build();

    // when
    couponService.use(userCoupon);

    // then
    verify(couponRepository, atLeastOnce()).save(userCoupon);
    assertThat(userCoupon.getIsUsed()).isTrue();
  }
}