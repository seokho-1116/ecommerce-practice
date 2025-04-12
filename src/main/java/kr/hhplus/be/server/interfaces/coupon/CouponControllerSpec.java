package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse.CouponSummaryResponse;

public interface CouponControllerSpec {

  @Operation(
      summary = "쿠폰 목록 조회",
      description = "쿠폰 목록을 조회합니다.",
      tags = {"쿠폰"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "쿠폰 목록 조회 성공",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "500",
              description = "서버 오류"
          )
      }
  )
  CommonResponseWrapper<List<CouponSummaryResponse>> findAllCoupons();

  @Operation(
      summary = "쿠폰 발급",
      description = "쿠폰을 발급합니다.",
      tags = {"쿠폰"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "쿠폰 발급 성공 또는 쿠폰 발급 실패",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "404",
              description = "사용자 쿠폰 정보 없음"
          ),
          @ApiResponse(
              responseCode = "500",
              description = "서버 오류"
          )
      }
  )
  CommonResponseWrapper<CouponIssueResponse> issue(@RequestBody CouponIssueRequest request);
}
