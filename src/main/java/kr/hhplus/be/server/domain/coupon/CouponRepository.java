package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.support.CacheKeyHolder;

public interface CouponRepository {

  Optional<UserCoupon> findUserCouponByUserCouponId(Long userCouponId);

  List<UserCoupon> findUserCouponsByUserId(Long userId);

  UserCoupon saveUserCoupon(UserCoupon userCoupon);

  Optional<Coupon> findById(Long couponId);

  Optional<Coupon> findForUpdateById(Long couponId);

  List<Coupon> findAllCoupons();

  Coupon save(Coupon coupon);

  void addQueue(CacheKeyHolder<Long> key, Long userId, long currentTimeMillis);

  List<Long> findAllUserIdInQueue(CacheKeyHolder<Long> key, long startInclusive, long endInclusive);

  void saveEventCoupon(Coupon coupon);

  Optional<UserCoupon> findUserCouponForUpdateByUserIdAndCouponId(Long userId, Long couponId);

  Optional<Coupon> findEventCoupon();
}
