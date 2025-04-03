package kr.hhplus.be.server.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record TopSellingProductResponse(

    LocalDateTime from,
    LocalDateTime to,
    List<TopSellingProduct> topSellingProducts
) {

  public record TopSellingProduct(
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
