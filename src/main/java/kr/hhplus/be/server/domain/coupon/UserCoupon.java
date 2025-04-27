package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.CouponBusinessException.CouponIllegalStateException;
import kr.hhplus.be.server.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Boolean isUsed;

  @Version
  private Long version;

  @ManyToOne
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  public long calculateDiscountPrice(long totalPrice) {
    return coupon.calculateDiscountPrice(totalPrice);
  }

  public void use() {
    if (Boolean.TRUE.equals(this.isUsed) || Boolean.FALSE.equals(coupon.getIsActive())) {
      throw new CouponIllegalStateException("이미 사용된 쿠폰입니다.");
    }

    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(coupon.getFrom()) || now.isAfter(coupon.getTo())) {
      throw new CouponIllegalStateException("쿠폰 사용 기간이 아닙니다.");
    }

    this.isUsed = true;
  }
}