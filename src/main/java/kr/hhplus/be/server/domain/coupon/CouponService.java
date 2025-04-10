package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Service;

@Service
public class CouponService {

  private CouponRepository couponRepository;

  public UserCoupon findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId);
  }

  public void use(UserCoupon userCoupon) {
    userCoupon.use();
    couponRepository.save(userCoupon);
  }
}
