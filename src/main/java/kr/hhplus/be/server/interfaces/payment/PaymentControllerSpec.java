package kr.hhplus.be.server.interfaces.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;

public interface PaymentControllerSpec {

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
  CommonResponseWrapper<OrderPaymentResponse> paymentOrder(
      @Parameter(in = ParameterIn.PATH, description = "주문 아이디", required = true) long orderId,
      @RequestBody OrderPaymentRequest request);
}
