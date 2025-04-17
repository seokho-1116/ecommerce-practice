package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;

  public UserCoupon findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
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

  public List<Coupon> findAllCoupons() {
    return couponRepository.findAllCoupons();
  }
}
