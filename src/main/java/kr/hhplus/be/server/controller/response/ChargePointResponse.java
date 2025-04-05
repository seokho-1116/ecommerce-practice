package kr.hhplus.be.server.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 응답")
public record ChargePointResponse(
    @Schema(description = "유저 ID")
    Long userId,

    @Schema(description = "포인트 잔액")
    Long amount
) {

}
