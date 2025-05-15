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
  public List<UserCoupon> findUserCouponsByUserId(Long userId) {
    return userCouponJpaRepository.findAllByUserId(userId);
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
  public Optional<Coupon> findByIdInCache(Long couponId) {
    String key = CacheKey.COUPON.appendAfterColon(String.valueOf(couponId));
    Coupon coupon = redisRepository.find(key, new TypeReference<>() {
    });
    if (coupon != null) {
      return Optional.of(coupon);
    }

    Optional<Coupon> optionalCoupon = couponJpaRepository.findById(couponId);
    optionalCoupon.ifPresent(value -> redisRepository.save(key, value));

    return optionalCoupon;
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
  public Coupon save(Coupon coupon) {
    return couponJpaRepository.save(coupon);
  }

  @Override
  public void addQueue(String key, Long userId, long currentTimeMillis) {
    redisRepository.saveIfAbsent(key, String.valueOf(userId), currentTimeMillis);
  }

  @Override
  public Optional<Coupon> findEventCoupon(String key) {
    Coupon coupon = redisRepository.find(key, new TypeReference<>() {
    });

    return Optional.ofNullable(coupon);
  }

  @Override
  public List<Long> findAllUserIdInQueue(String key, long startInclusive, long endExclusive) {
    return redisRepository.findRangeInZset(key, startInclusive, endExclusive, new TypeReference<>() {
    });
  }

  @Override
  public void saveInCache(String key, Coupon coupon) {
    redisRepository.save(key, coupon);
  }

  @Override
  public void deleteEventCoupon(String key) {
    redisRepository.delete(key);
  }

  @Override
  public Optional<UserCoupon> findUserCouponForUpdateByUserIdAndCouponId(Long userId, Long couponId) {
    return userCouponJpaRepository.findUserCouponByUserIdAndCouponId(userId, couponId);
  }
}
