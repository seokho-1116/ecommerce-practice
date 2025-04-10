package kr.hhplus.be.server.domain.coupon;

public interface CouponRepository {

  UserCoupon findUserCouponByUserCouponId(Long userCouponId);

  void saveUserCoupon(UserCoupon userCoupon);
}
