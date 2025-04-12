package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;

public record ProductDto() {

  public record Top5SellingProducts(
      LocalDateTime from,
      LocalDateTime to,
      List<ProductWithRank> topSellingProducts
  ) {

  }

  public record ProductWithRank(
      Long rank,
      Long quantity,
      Long productId,
      String name,
      String description,
      Long basePrice
  ) {

  }

  public record ProductWithQuantity(
      Long id,
      String name,
      String description,
      Long basePrice,
      List<ProductWithQuantityOption> options
  ) {

    public record ProductWithQuantityOption(
        Long id,
        String name,
        String description,
        Long additionalPrice,
        Long quantity
    ) {

    }

  }
}
