package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.order.OrderResponse.OrderSuccessResponse;

public interface OrderControllerSpec {

  @Operation(
      summary = "주문 생성",
      description = "주문을 생성합니다.",
      tags = {"주문"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "주문 생성 성공",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "쿠폰 사용 실패 - 유효한 쿠폰이 아님",
              content = @Content(
                  mediaType = "application/json"
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "사용자 정보 없음",
              content = @Content(
                  mediaType = "application/json"
              )
          ),
          @ApiResponse(
              responseCode = "500",
              description = "서버 오류",
              content = @Content(
                  mediaType = "application/json"
              )
          )
      })
  CommonResponseWrapper<OrderSuccessResponse> createOrder(@RequestBody OrderRequest request);
}
