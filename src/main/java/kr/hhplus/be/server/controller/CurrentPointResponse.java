package kr.hhplus.be.server.controller;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 조회 응답")
public record CurrentPointResponse(
    @Schema(description = "유저 ID")
    Long userId,

    @Schema(description = "포인트 잔액")
    Long amount
) {

}
