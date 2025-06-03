package kr.hhplus.be.server.domain.coupon;

public class CouponEvent {

  private CouponEvent() {
  }

  public record CouponIssueEvent(
      Long userId,
      Long couponId
  ) {
    public static CouponIssueEvent of(Long userId, Long couponId) {
      return new CouponIssueEvent(userId, couponId);
    }
  }
}
