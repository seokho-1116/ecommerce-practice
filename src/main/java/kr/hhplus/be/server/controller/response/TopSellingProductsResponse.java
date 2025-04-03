package kr.hhplus.be.server.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record TopSellingProductsResponse(

    LocalDateTime from,
    LocalDateTime to,
    List<TopSellingProductResponse> topSellingProducts
) {

  public record TopSellingProductResponse(
      Long rank,
      Long productId,
      Long productOptionId,
      String name,
      String description,
      Long basePrice,
      Long additionalPrice,
      Long quantity
  ) {

  }
}
