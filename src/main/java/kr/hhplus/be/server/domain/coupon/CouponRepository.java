package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

  Optional<UserCoupon> findUserCouponByUserCouponId(Long userCouponId);

  UserCoupon saveUserCoupon(UserCoupon userCoupon);

  Optional<Coupon> findById(Long couponId);

  Optional<Coupon> findForUpdateById(Long couponId);

  List<Coupon> findAllCoupons();

  void save(Coupon coupon);

  void addQueue(String key, Long userId, long currentTimeMillis);
}
