package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponIllegalStateException;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Coupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private Double discountRate;
  private Long discountAmount;
  private Long quantity;

  @Enumerated(EnumType.STRING)
  private CouponType couponType;
  private LocalDateTime from;
  private LocalDateTime to;

  @Enumerated(EnumType.STRING)
  private CouponStatus couponStatus;

  @Builder
  public Coupon(Long id, String name, String description, Double discountRate, Long discountAmount,
      Long quantity, CouponType couponType, LocalDateTime from, LocalDateTime to,
      CouponStatus couponStatus
  ) {
    if (from == null || to == null) {
      throw new CouponIllegalStateException("쿠폰 사용 기간이 설정되어야 합니다.");
    }

    if (from.isAfter(to)) {
      throw new CouponIllegalStateException("쿠폰 사용 기간이 잘못 설정되었습니다. 시작일이 종료일보다 늦을 수 없습니다.");
    }

    if (couponType == null) {
      throw new CouponIllegalStateException("쿠폰 타입이 설정되어야 합니다.");
    }

    if (CouponType.PERCENTAGE.equals(couponType) && (discountRate == null || discountRate < 0)) {
      throw new CouponIllegalStateException("비율 쿠폰인 경우 할인율은 0 이상이어야 합니다.");
    }

    if (CouponType.FIXED.equals(couponType) && (discountAmount == null || discountAmount < 0)) {
      throw new CouponIllegalStateException("정액 쿠폰인 경우 할인 금액은 0 이상이어야 합니다.");
    }

    if (quantity != null && quantity < 0) {
      throw new CouponIllegalStateException("쿠폰 수량은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.discountRate = discountRate;
    this.discountAmount = discountAmount;
    this.quantity = quantity;
    this.couponType = couponType;
    this.from = from;
    this.to = to;
    this.couponStatus = couponStatus;
  }

  public long calculateDiscountPrice(long totalPrice) {
    return couponType.calculateDiscountPrice(totalPrice, discountRate, discountAmount);
  }

  public UserCoupon issue(User user) {
    if (quantity != null && quantity <= 0) {
      throw new CouponIllegalStateException("쿠폰 수량이 부족합니다.");
    }

    if (quantity != null) {
      this.quantity--;
    }

    return UserCoupon.builder()
        .userId(user.getId())
        .isUsed(false)
        .coupon(this)
        .build();
  }

  public void updateCouponStatus(CouponStatus couponStatus) {
    this.couponStatus = couponStatus;
  }

  public boolean isNotAvailableForIssue() {
    LocalDateTime now = LocalDateTime.now();
    return !CouponStatus.AVAILABLE.equals(couponStatus)
        || now.isBefore(from)
        || now.isAfter(to)
        || quantity == null
        || quantity <= 0;
  }

  public void deductQuantity(int size) {
    if (quantity == null || quantity < size) {
      throw new CouponIllegalStateException("쿠폰 수량이 부족합니다.");
    }

    this.quantity -= size;
    if (quantity <= 0) {
      this.couponStatus = CouponStatus.COMPLETE;
    }
  }
}