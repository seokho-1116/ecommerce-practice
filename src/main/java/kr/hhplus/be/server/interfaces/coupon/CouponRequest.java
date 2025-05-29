package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponEventCommand;
import kr.hhplus.be.server.domain.coupon.CouponStatus;
import kr.hhplus.be.server.domain.coupon.CouponType;

public class CouponRequest {

  CouponRequest() {
  }

  public record CouponIssueRequest(
      @NotNull
      Long userId,

      @NotNull
      Long couponId
  ) {

  }

  public record CouponEventRequest(
      @NotBlank(message = "쿠폰 이름은 필수입니다.")
      String name,

      String description,

      @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
      @Max(value = 100, message = "할인율은 100 이하여야 합니다.")
      Double discountRate,

      @Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
      Long discountAmount,

      @NotNull(message = "수량은 필수입니다.")
      Long quantity,

      @NotNull(message = "쿠폰 타입은 필수입니다.")
      CouponType couponType,

      @NotNull(message = "유효 시작일은 필수입니다.")
      LocalDateTime from,

      @NotNull(message = "유효 종료일은 필수입니다.")
      LocalDateTime to,

      @NotNull(message = "쿠폰 상태는 필수입니다.")
      CouponStatus couponStatus
  ) {

    public CouponEventCommand toCommand() {
      return CouponEventCommand.builder()
          .name(name)
          .description(description)
          .discountRate(discountRate)
          .discountAmount(discountAmount)
          .quantity(quantity)
          .couponType(couponType)
          .from(from)
          .to(to)
          .couponStatus(couponStatus)
          .build();
    }
  }
}
