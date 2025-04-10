package kr.hhplus.be.server.domain.coupon;

import java.util.Optional;

public interface CouponRepository {

  UserCoupon findUserCouponByUserCouponId(Long userCouponId);

  UserCoupon saveUserCoupon(UserCoupon userCoupon);

  Optional<Coupon> findById(Long couponId);
}
