package kr.hhplus.be.server.domain.coupon;

import static org.instancio.Select.field;

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

  public UserCoupon userCoupon(User user, Coupon coupon) {
    return Instancio.of(UserCoupon.class)
        .ignore(field(UserCoupon::getId))
        .set(field(UserCoupon::getIsActive), true)
        .ignore(field(UserCoupon::getCreatedAt))
        .ignore(field(UserCoupon::getUpdatedAt))
        .set(field(UserCoupon::getUser), user)
        .set(field(UserCoupon::getCoupon), coupon)
        .create();
  }
}
