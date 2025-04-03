package kr.hhplus.be.server.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.OrderPaymentRequest;
import kr.hhplus.be.server.controller.response.OrderPaymentResponse;
import kr.hhplus.be.server.controller.response.OrderRequest;
import kr.hhplus.be.server.controller.response.OrderResponse;

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
  CommonResponseWrapper<OrderResponse> createOrder(OrderRequest request);

  @Operation(
      summary = "주문 결제",
      description = "주문을 결제합니다.",
      tags = {"주문"}
  )
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "200",
              description = "주문 결제 성공",
              useReturnTypeSchema = true
          ),
          @ApiResponse(
              responseCode = "400",
              description = "재고가 부족하거나 잔액이 부족하여 결제 실패",
              content = @Content(
                  mediaType = "application/json"
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "주문을 찾을 수 없음",
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
  CommonResponseWrapper<OrderPaymentResponse> paymentOrder(long orderId,
      OrderPaymentRequest request);
}
