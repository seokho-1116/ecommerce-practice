package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private final CouponJpaRepository couponJpaRepository;
  private final UserCouponJpaRepository userCouponJpaRepository;

  @Override
  public Optional<UserCoupon> findUserCouponByUserCouponId(Long userCouponId) {
    return userCouponJpaRepository.findById(userCouponId);
  }

  @Override
  public UserCoupon saveUserCoupon(UserCoupon userCoupon) {
    return userCouponJpaRepository.save(userCoupon);
  }

  @Override
  public Optional<Coupon> findById(Long couponId) {
    return couponJpaRepository.findById(couponId);
  }

  @Override
  public List<Coupon> findAllCoupons() {
    return couponJpaRepository.findAll();
  }
}
