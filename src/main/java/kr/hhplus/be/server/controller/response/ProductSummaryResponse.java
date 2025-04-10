package kr.hhplus.be.server.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;

public record ProductSummaryResponse(
    @Schema(description = "상품 ID")
    Long id,

    @Schema(description = "상품 이름")
    String name,

    @Schema(description = "상품 설명")
    String description,

    @Schema(description = "상품 기본 가격")
    Long basePrice,

    @Schema(description = "상품 옵션 목록")
    List<SummaryOption> options
) {

  public static List<ProductSummaryResponse> from(List<ProductWithQuantity> productWithQuantities) {
    return productWithQuantities.stream()
        .map(productWithQuantity -> new ProductSummaryResponse(
            productWithQuantity.id(),
            productWithQuantity.name(),
            productWithQuantity.description(),
            productWithQuantity.basePrice(),
            productWithQuantity.options()
                .stream()
                .map(option -> new SummaryOption(
                    option.id(),
                    option.name(),
                    option.description(),
                    option.additionalPrice(),
                    option.quantity()
                ))
                .toList()
        ))
        .toList();
  }

  public record SummaryOption(
      @Schema(description = "상품 옵션 ID")
      Long id,

      @Schema(description = "상품 옵션 이름")
      String name,

      @Schema(description = "상품 옵션 설명")
      String description,

      @Schema(description = "상품 옵션 추가 가격")
      Long additionalPrice,

      @Schema(description = "상품 옵션 재고")
      Long inventory
  ) {

  }
}
