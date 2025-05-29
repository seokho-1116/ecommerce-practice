package kr.hhplus.be.server.infrastructure.coupon;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCacheKey;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.infrastructure.support.RedisRepository;
import kr.hhplus.be.server.support.CacheKeyHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

  private static final String LOCAL_CACHE_NAME = "coupon";

  private final CouponJpaRepository couponJpaRepository;
  private final UserCouponJpaRepository userCouponJpaRepository;
  private final CacheManager cacheManager;
  private final RedisRepository redisRepository;
  private final UserCouponCustomRepository userCouponCustomRepository;

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
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON.value(couponId);
    String generated = key.generate();

    Cache cache = cacheManager.getCache(LOCAL_CACHE_NAME);
    if (cache != null) {
      Coupon cached = cache.get(generated, Coupon.class);

      if (cached != null) {
        return Optional.of(cached);
      }
    }

    Coupon coupon = redisRepository.find(generated, new TypeReference<>() {
    });
    if (coupon != null) {
      return Optional.of(coupon);
    }

    Optional<Coupon> optionalCoupon = couponJpaRepository.findById(couponId);
    optionalCoupon.ifPresent(value -> redisRepository.save(key.generate(), value));

    return optionalCoupon;
  }

  @Override
  public List<Coupon> findAllCoupons() {
    return couponJpaRepository.findAll();
  }

  @Override
  public Coupon save(Coupon coupon) {
    Coupon saved = couponJpaRepository.save(coupon);

    CacheKeyHolder<Long> key = CouponCacheKey.COUPON.value(saved.getId());
    redisRepository.save(key.generate(), saved);

    return saved;
  }

  @Override
  public void saveEventCoupon(Coupon coupon) {
    CacheKeyHolder<Long> key = CouponCacheKey.COUPON.value(coupon.getId());
    String generated = key.generate();

    Cache cache = cacheManager.getCache(LOCAL_CACHE_NAME);
    if (cache != null) {
      cache.put(generated, coupon);
    }

    redisRepository.save(CouponCacheKey.COUPON_EVENT.generate(null), coupon);
  }

  @Override
  public List<UserCoupon> findAllUserCouponsByUserIdAndOrderId(Long userId, Long orderId) {
    return userCouponJpaRepository.findAllByUserIdAndOrderId(userId, orderId);
  }

  @Override
  public List<UserCoupon> findUserCouponsByCouponId(Long couponId) {
    return userCouponJpaRepository.findAllByCouponId(couponId);
  }

  @Override
  public void saveAllUserCoupons(List<UserCoupon> userCoupons) {
    userCouponCustomRepository.saveAll(userCoupons);
  }
}
