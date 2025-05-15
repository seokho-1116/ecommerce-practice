package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {

  Optional<UserCoupon> findUserCouponByUserCouponId(Long userCouponId);

  List<UserCoupon> findUserCouponsByUserId(Long userId);

  UserCoupon saveUserCoupon(UserCoupon userCoupon);

  Optional<Coupon> findById(Long couponId);

  Optional<Coupon> findByIdInCache(Long couponId);

  Optional<Coupon> findForUpdateById(Long couponId);

  List<Coupon> findAllCoupons();

  Coupon save(Coupon coupon);

  void addQueue(String key, Long userId, long currentTimeMillis);

  Optional<Coupon> findEventCoupon(String key);

  List<Long> findAllUserIdInQueue(String key, long startInclusive, long endInclusive);

  void saveInCache(String key, Coupon coupon);

  Optional<UserCoupon> findUserCouponForUpdateByUserIdAndCouponId(Long userId, Long couponId);

  void deleteEventCoupon(String key);
}
