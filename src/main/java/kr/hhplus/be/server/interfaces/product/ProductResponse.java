package kr.hhplus.be.server.interfaces.product;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;

public record ProductResponse(

) {

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
      List<ProductSummaryResponse.SummaryOptionResponse> options
  ) {

    public static List<ProductSummaryResponse> from(
        List<ProductWithQuantity> productWithQuantities) {
      return productWithQuantities.stream()
          .map(productWithQuantity -> new ProductSummaryResponse(
              productWithQuantity.id(),
              productWithQuantity.name(),
              productWithQuantity.description(),
              productWithQuantity.basePrice(),
              productWithQuantity.options().stream()
                  .map(option -> new SummaryOptionResponse(
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

    public record SummaryOptionResponse(
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

  public record TopSellingProductsResponse(

      @Schema(description = "상위 상품 집계일")
      LocalDate date,

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
          top5SellingProducts.date(),
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
}
