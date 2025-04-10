package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

  private CouponRepository couponRepository;

  public UserCoupon findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId);
  }

  public void use(UserCoupon userCoupon) {
    userCoupon.use();
    couponRepository.saveUserCoupon(userCoupon);
  }

  public UserCoupon issue(User user, Long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    UserCoupon userCoupon = coupon.issue(user);
    return couponRepository.saveUserCoupon(userCoupon);
  }
}
