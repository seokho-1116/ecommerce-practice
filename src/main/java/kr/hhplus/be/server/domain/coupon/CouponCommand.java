package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.CouponEvent.CouponIssueEvent;
import lombok.Builder;

public class CouponCommand {

  @Builder
  public record CouponEventCommand(
      String name,
      String description,
      Double discountRate,
      Long discountAmount,
      Long quantity,
      CouponType couponType,
      LocalDateTime from,
      LocalDateTime to,
      CouponStatus couponStatus
  ) {

  }

  public record CouponIssueCommand(
      Long userId,
      Long couponId
  ) {

    public static CouponIssueCommand from(CouponIssueEvent event) {
      return new CouponIssueCommand(event.userId(), event.couponId());
    }
  }
}
