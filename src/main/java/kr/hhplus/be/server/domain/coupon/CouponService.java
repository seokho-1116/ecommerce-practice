package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponNotFoundException;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponEventCommand;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponEvent.CouponIssueEvent;
import kr.hhplus.be.server.infrastructure.coupon.CouponEventPublisher;
import kr.hhplus.be.server.support.CacheKeyHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class CouponService {

  private final TransactionTemplate transactionTemplate;
  private final CouponRepository couponRepository;
  private final CouponEventPublisher couponEventPublisher;

  public UserCouponInfo findUserCouponByUserCouponId(Long userCouponId) {
    return couponRepository.findUserCouponByUserCouponId(userCouponId)
        .map(UserCouponInfo::from)
        .orElse(null);
  }

  @Transactional
  public void use(Long userId, Long orderId) {
    List<UserCoupon> userCoupons = couponRepository.findAllUserCouponsByUserIdAndOrderId(userId,
        orderId);

    for (UserCoupon userCoupon : userCoupons) {
      userCoupon.use();
      couponRepository.saveUserCoupon(userCoupon);
    }
  }

  public CouponIssueInfo issue(Long userId, Long couponId) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    if (coupon.isNotAvailableForIssue()) {
      throw new CouponBusinessException("쿠폰이 발급 가능한 상태가 아닙니다.");
    }

    CouponIssueEvent event = CouponIssueEvent.of(userId, coupon.getId());
    couponEventPublisher.issueCoupon(event);

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

  public void reserveCouponForOrder(Long userCouponId, Long orderId) {
    if (userCouponId == null) {
      return;
    }

    couponRepository.findUserCouponByUserCouponId(userCouponId)
        .ifPresent(userCoupon -> {
          userCoupon.reserve(orderId);
          couponRepository.saveUserCoupon(userCoupon);
        });
  }

  @Transactional
  public void issueAllFromQueue(Long couponId, List<CouponIssueCommand> commands) {
    Coupon coupon = couponRepository.findById(couponId)
        .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));

    if (coupon.isNotAvailableForIssue()) {
      throw new CouponBusinessException("쿠폰이 발급 가능한 상태가 아닙니다.");
    }

    List<CouponIssueCommand> filteredCommands = commands.stream()
        .limit(coupon.getQuantity())
        .toList();

    List<Pair<Long, Long>> userIdAndCouponIdPairs = filteredCommands.stream()
        .map(command -> Pair.of(command.userId(), command.couponId()))
        .toList();
    Set<Long> alreadyIssuedUsers = couponRepository.findUserCouponsByUserIdAndCouponIdIn(
            userIdAndCouponIdPairs).stream()
        .map(UserCoupon::getUserId)
        .collect(Collectors.toSet());

    List<UserCoupon> newUserCoupons = filteredCommands.stream()
        .filter(command -> !alreadyIssuedUsers.contains(command.userId()))
        .map(command -> UserCoupon.builder()
            .userId(command.userId())
            .coupon(coupon)
            .build())
        .toList();
    couponRepository.saveAllUserCoupons(newUserCoupons);

    coupon.deductQuantity(newUserCoupons.size());
    couponRepository.save(coupon);
  }
}