package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.ErrorResponse;
import kr.hhplus.be.server.interfaces.point.PointResponse.ChargePointResponse;
import kr.hhplus.be.server.interfaces.point.PointResponse.CurrentPointResponse;

public interface PointControllerSpec {

  @Operation(
      summary = "포인트 충전",
      description = "사용자의 포인트를 충전합니다.",
      tags = {"포인트"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "포인트 충전 성공",
          useReturnTypeSchema = true
      ),
      @ApiResponse(
          responseCode = "400",
          description = "최대 포인트(1,000,000) 위반으로 충전 실패",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ErrorResponse.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자 정보 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ErrorResponse.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ErrorResponse.class
              )
          )
      )
  })
  CommonResponseWrapper<ChargePointResponse> chargePoint(
      @Parameter(in = ParameterIn.PATH, description = "사용자 아이디", required = true) long id,
      @RequestBody long amount);

  @Operation(
      summary = "포인트 조회",
      description = "사용자의 포인트를 조회합니다.",
      tags = {"포인트"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "포인트 조회 성공",
          useReturnTypeSchema = true
      ),
      @ApiResponse(
          responseCode = "404",
          description = "사용자 정보 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ErrorResponse.class
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "서버 오류",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = ErrorResponse.class
              )
          )
      )
  })
  CommonResponseWrapper<CurrentPointResponse> findPoint(
      @Parameter(in = ParameterIn.PATH, description = "사용자 아이디", required = true) long id);
}