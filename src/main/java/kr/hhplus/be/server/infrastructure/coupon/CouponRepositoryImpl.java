package kr.hhplus.be.server.infrastructure.coupon;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.infrastructure.support.RedisRepository;
import kr.hhplus.be.server.support.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private final CouponJpaRepository couponJpaRepository;
  private final UserCouponJpaRepository userCouponJpaRepository;
  private final RedisRepository redisRepository;

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
    CacheKey cacheKey = CacheKey.COUPON;
    String key = cacheKey.appendAfterColon(String.valueOf(couponId));
    Coupon coupon = redisRepository.find(key, new TypeReference<>() {
    });
    if (coupon != null) {
      return Optional.of(coupon);
    }

    return couponJpaRepository.findById(couponId);
  }

  @Override
  public Optional<Coupon> findForUpdateById(Long couponId) {
    return couponJpaRepository.findForUpdateById(couponId);
  }

  @Override
  public List<Coupon> findAllCoupons() {
    return couponJpaRepository.findAll();
  }

  @Override
  public void save(Coupon coupon) {
    couponJpaRepository.save(coupon);
  }

  @Override
  public void addQueue(String key, Long userId, long currentTimeMillis) {
    redisRepository.addIfAbsent(key, String.valueOf(userId), currentTimeMillis);
  }
}
