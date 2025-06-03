package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

  Optional<UserCoupon> findUserCouponByUserCouponId(Long userCouponId);

  UserCoupon saveUserCoupon(UserCoupon userCoupon);

  Optional<Coupon> findById(Long couponId);

  List<Coupon> findAllCoupons();

  Coupon save(Coupon coupon);

  void saveEventCoupon(Coupon coupon);

  List<UserCoupon> findAllUserCouponsByUserIdAndOrderId(Long userId, Long orderId);

  List<UserCoupon> findUserCouponsByCouponId(Long couponId);

  void saveAllUserCoupons(List<UserCoupon> userCoupons);
}
