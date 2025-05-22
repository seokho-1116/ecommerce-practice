package kr.hhplus.be.server.domain.coupon;

import static org.instancio.Select.field;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.user.User;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class CouponTestDataGenerator {

  public Coupon coupon() {
    return Instancio.of(Coupon.class)
        .ignore(field(Coupon::getId))
        .set(field(Coupon::getIsActive), true)
        .ignore(field(Coupon::getCreatedAt))
        .ignore(field(Coupon::getUpdatedAt))
        .create();
  }

  public Coupon validCoupon() {
    LocalDateTime now = LocalDateTime.now();
    return Instancio.of(Coupon.class)
        .ignore(field(Coupon::getId))
        .set(field(Coupon::getIsActive), true)
        .set(field(Coupon::getFrom), now.minusDays(3))
        .set(field(Coupon::getTo), now.plusDays(3))
        .ignore(field(Coupon::getCreatedAt))
        .ignore(field(Coupon::getUpdatedAt))
        .create();
  }

  public Coupon validLimitedCoupon(Long availableCount) {
    LocalDateTime now = LocalDateTime.now();
    return Instancio.of(Coupon.class)
        .ignore(field(Coupon::getId))
        .set(field(Coupon::getIsActive), true)
        .set(field(Coupon::getFrom), now.minusDays(3))
        .set(field(Coupon::getTo), now.plusDays(3))
        .set(field(Coupon::getQuantity), availableCount)
        .ignore(field(Coupon::getCreatedAt))
        .ignore(field(Coupon::getUpdatedAt))
        .create();
  }

  public UserCoupon userCoupon(User user, Coupon coupon) {
    return Instancio.of(UserCoupon.class)
        .ignore(field(UserCoupon::getId))
        .set(field(UserCoupon::getIsActive), true)
        .ignore(field(UserCoupon::getCreatedAt))
        .ignore(field(UserCoupon::getUpdatedAt))
        .set(field(UserCoupon::getUserId), user.getId())
        .set(field(UserCoupon::getCoupon), coupon)
        .create();
  }

  public UserCoupon notUsedUserCoupon(User user, Coupon coupon) {
    return Instancio.of(UserCoupon.class)
        .ignore(field(UserCoupon::getId))
        .set(field(UserCoupon::getIsActive), true)
        .set(field(UserCoupon::getIsUsed), false)
        .ignore(field(UserCoupon::getCreatedAt))
        .ignore(field(UserCoupon::getUpdatedAt))
        .set(field(UserCoupon::getUserId), user.getId())
        .set(field(UserCoupon::getCoupon), coupon)
        .create();
  }
}
