package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.support.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final TransactionTemplate transactionTemplate;
  private final CouponRepository couponRepository;

  public UserCouponInfo findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId)
        .map(UserCouponInfo::from)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
  }

  @Transactional
  public void use(Long userCouponId) {
    UserCoupon userCoupon = couponRepository.findUserCouponByUserCouponId(userCouponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    userCoupon.use();
    couponRepository.saveUserCoupon(userCoupon);
  }

  public CouponIssueInfo issue(Long userId, Long couponId) {
    Coupon coupon = couponRepository.findByIdInCache(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    couponRepository.addQueue(CacheKey.COUPON_EVENT_QUEUE.getKey(), userId,
        System.currentTimeMillis());

    return CouponIssueInfo.from(userId, coupon.getId());
  }

  public List<Coupon> findAllCoupons() {
    return couponRepository.findAllCoupons();
  }

  @Transactional
  public CouponInfo saveCouponEvent(CouponEventCommand event) {
    Coupon coupon = Coupon.builder()
        .name(event.name())
        .description(event.description())
        .quantity(event.quantity())
        .from(event.from())
        .to(event.to())
        .couponType(event.couponType())
        .discountRate(event.discountRate())
        .discountAmount(event.discountAmount())
        .couponStatus(CouponStatus.AVAILABLE)
        .build();

    Coupon savedCoupon = couponRepository.save(coupon);

    String key = CacheKey.COUPON.appendAfterColon(String.valueOf(savedCoupon.getId()));
    couponRepository.saveInCache(key, savedCoupon);
    couponRepository.saveInCache(CacheKey.COUPON_EVENT.getKey(), savedCoupon);

    return CouponInfo.from(savedCoupon);
  }

  public void issueAvailableCoupons() {
    Optional<Coupon> optionalCouponEvent = couponRepository.findEventCoupon(
        CacheKey.COUPON_EVENT.getKey());
    if (optionalCouponEvent.isEmpty()) {
      return;
    }

    Coupon coupon = optionalCouponEvent.get();
    if (coupon.isNotAvailableForIssue()) {
      return;
    }

    List<Long> userIds = couponRepository.findAllUserIdInQueue(CacheKey.COUPON_EVENT_QUEUE.getKey(),
        0, coupon.getQuantity() - 1);

    AtomicInteger savedCount = new AtomicInteger();
    for (Long userId : userIds) {
      transactionTemplate.executeWithoutResult(status -> {
        Optional<UserCoupon> optionalUserCoupon = couponRepository.findUserCouponForUpdateByUserIdAndCouponId(
            userId, coupon.getId());

        if (optionalUserCoupon.isEmpty()) {
          UserCoupon newUserCoupon = UserCoupon.builder()
              .userId(userId)
              .coupon(coupon)
              .build();
          couponRepository.saveUserCoupon(newUserCoupon);

          savedCount.getAndIncrement();
        }
      });
    }

    if (couponEventComplete(userIds, coupon, savedCount.get())) {
      transactionTemplate.executeWithoutResult(
          status -> couponRepository.findForUpdateById(coupon.getId())
              .ifPresent(eventCoupon -> {
                eventCoupon.updateCouponStatus(CouponStatus.COMPLETE);
                couponRepository.deleteEventCoupon(CacheKey.COUPON_EVENT.getKey());
                couponRepository.save(eventCoupon);
              }));
    }
  }

  private boolean couponEventComplete(List<Long> userIds, Coupon coupon, int savedCount) {
    return userIds.size() >= coupon.getQuantity() && (savedCount == 0
        || savedCount == coupon.getQuantity());
  }
}