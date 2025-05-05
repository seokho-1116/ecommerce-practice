package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final CouponRepository couponRepository;

  public UserCouponInfo findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId)
        .map(UserCouponInfo::from)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
  }

  @Transactional
  public void use(Long userCouponId) {
    UserCoupon userCoupon = couponRepository.findUserCouponByUserCouponId(userCouponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    userCoupon.use();
    couponRepository.saveUserCoupon(userCoupon);
  }

  @Transactional
  public UserCoupon issue(User user, Long couponId) {
    Coupon coupon = couponRepository.findForUpdateById(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    UserCoupon userCoupon = coupon.issue(user);
    couponRepository.save(coupon);

    return couponRepository.saveUserCoupon(userCoupon);
  }

  public List<Coupon> findAllCoupons() {
    return couponRepository.findAllCoupons();
  }
}
