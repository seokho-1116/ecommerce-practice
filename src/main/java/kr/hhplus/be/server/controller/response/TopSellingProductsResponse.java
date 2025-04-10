package kr.hhplus.be.server.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;

public record TopSellingProductsResponse(

    @Schema(description = "상위 상품 집계 시작 시간")
    LocalDateTime from,

    @Schema(description = "상위 상품 집계 종료 시간")
    LocalDateTime to,

    @Schema(description = "상위 판매 상품 목록")
    List<TopSellingProduct> topSellingProducts
) {

  public static TopSellingProductsResponse from(Top5SellingProducts top5SellingProducts) {
    List<TopSellingProduct> topSellingProductResponses = top5SellingProducts.topSellingProducts()
        .stream()
        .map(productWithRank -> new TopSellingProduct(
            productWithRank.rank(),
            productWithRank.productId(),
            productWithRank.name(),
            productWithRank.description(),
            productWithRank.basePrice(),
            productWithRank.quantity()
        ))
        .toList();

    return new TopSellingProductsResponse(
        top5SellingProducts.from(),
        top5SellingProducts.to(),
        topSellingProductResponses
    );
  }

  public record TopSellingProduct(
      @Schema(description = "랭킹")
      Long rank,

      @Schema(description = "상품 ID")
      Long productId,

      @Schema(description = "상품 이름")
      String name,

      @Schema(description = "상품 설명")
      String description,

      @Schema(description = "상품 기본 가격")
      Long basePrice,

      @Schema(description = "상품 수량")
      Long quantity
  ) {

  }
}
