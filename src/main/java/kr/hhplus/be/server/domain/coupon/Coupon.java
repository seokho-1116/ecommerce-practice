package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponIllegalStateException;
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
  private CouponType couponType;
  private LocalDateTime fromTs;
  private LocalDateTime toTs;

  @Builder
  public Coupon(Long id, String name, String description, Double discountRate, Long discountAmount,
      CouponType couponType, LocalDateTime fromTs, LocalDateTime toTs) {
    if (fromTs == null || toTs == null) {
      throw new CouponIllegalStateException("쿠폰 사용 기간이 설정되어야 합니다.");
    }

    if (fromTs.isAfter(toTs)) {
      throw new CouponIllegalStateException("쿠폰 사용 기간이 잘못 설정되었습니다.");
    }

    if (couponType == null) {
      throw new CouponIllegalStateException("쿠폰 타입이 설정되어야 합니다.");
    }

    if (CouponType.PERCENTAGE.equals(couponType) && (discountRate == null || discountRate < 0)) {
       throw new CouponIllegalStateException("할인율은 0 이상이어야 합니다.");
    }

    if (CouponType.FIXED.equals(couponType) && (discountAmount == null || discountAmount < 0)) {
      throw new CouponIllegalStateException("할인 금액은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.discountRate = discountRate;
    this.discountAmount = discountAmount;
    this.couponType = couponType;
    this.fromTs = fromTs;
    this.toTs = toTs;
  }

  public long calculateDiscountPrice(long totalPrice) {
    return couponType.calculateDiscountPrice(totalPrice, discountRate, discountAmount);
  }
}