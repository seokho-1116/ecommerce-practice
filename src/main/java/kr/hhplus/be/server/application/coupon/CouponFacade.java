package kr.hhplus.be.server.application.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponFacade {

  private final UserService userService;
  private final CouponService couponService;

  @Transactional
  public UserCouponInfo issue(Long userId, Long couponId) {
    User user = userService.findUserById(userId);

    return couponService.issue(user, couponId);
  }
}
