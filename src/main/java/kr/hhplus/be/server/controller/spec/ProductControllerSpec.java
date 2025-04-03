package kr.hhplus.be.server.controller.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.ErrorResponse;
import kr.hhplus.be.server.controller.response.ProductSummaryResponse;

public interface ProductControllerSpec {

  @Operation(
      summary = "상품 목록 조회",
      description = "상품 목록을 조회합니다.",
      tags = {"상품"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "상품 목록 조회 성공",
          useReturnTypeSchema = true
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
  CommonResponseWrapper<List<ProductSummaryResponse>> findAllProducts();
}
