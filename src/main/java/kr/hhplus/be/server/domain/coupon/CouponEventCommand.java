package kr.hhplus.be.server.domain.coupon;

import java.time.LocalDateTime;
import lombok.Builder;

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
