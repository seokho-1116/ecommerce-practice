package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.support.CacheKeyHolder;
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
        .orElse(null);
  }

  @Transactional
  public void use(Long userCouponId) {
    UserCoupon userCoupon = couponRepository.findUserCouponByUserCouponId(userCouponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    userCoupon.use();
    couponRepository.saveUserCoupon(userCoupon);
  }

  public CouponIssueInfo issue(Long userId, Long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    CacheKeyHolder<Long> key = CouponCacheKey.COUPON_EVENT_QUEUE.value(coupon.getId());
    couponRepository.addQueue(key, userId, System.currentTimeMillis());

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
    couponRepository.saveEventCoupon(coupon);

    return CouponInfo.from(savedCoupon);
  }

  @Transactional
  public void issueCouponFromQueue() {
    Optional<Coupon> optionalCouponEvent = couponRepository.findEventCoupon();
    if (optionalCouponEvent.isEmpty()) {
      return;
    }

    Coupon coupon = optionalCouponEvent.get();
    if (coupon.isNotAvailableForIssue()) {
      return;
    }

    CacheKeyHolder<Long> key = CouponCacheKey.COUPON_EVENT_QUEUE.value(coupon.getId());
    List<Long> userIds = couponRepository.findAllUserIdInQueue(key, 0, coupon.getQuantity());

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
                couponRepository.saveEventCoupon(eventCoupon);
                couponRepository.save(eventCoupon);
              }));
    }
  }

  private boolean couponEventComplete(List<Long> userIds, Coupon coupon, int savedCount) {
    return userIds.size() >= coupon.getQuantity() && (savedCount == 0
        || savedCount == coupon.getQuantity());
  }
}