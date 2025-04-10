package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.UserPoint;

public record PointResponse(

) {

  @Schema(description = "포인트 충전 응답")
  public record ChargePointResponse(
      @Schema(description = "유저 ID")
      Long userId,

      @Schema(description = "포인트 잔액")
      Long amount
  ) {

  }

  @Schema(description = "포인트 조회 응답")
  public record CurrentPointResponse(
      @Schema(description = "유저 ID")
      Long userId,

      @Schema(description = "포인트 잔액")
      Long amount
  ) {

    public static CurrentPointResponse from(UserPoint userPoint) {
      return new CurrentPointResponse(
          userPoint.getUser().getId(),
          userPoint.getAmount()
      );
    }
  }
}
