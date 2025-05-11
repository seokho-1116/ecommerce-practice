package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity.ProductWithQuantityOption;

public record ProductDto() {

  public record Top5SellingProducts(
      LocalDateTime from,
      LocalDateTime to,
      List<ProductWithRank> topSellingProducts
  ) {

    public boolean isEmpty() {
      return topSellingProducts == null || topSellingProducts.isEmpty();
    }
  }

  public record ProductWithRank(
      Long rank,
      Long quantity,
      Long productId,
      String name,
      String description,
      Long basePrice
  ) {

    public static ProductWithRank of(ProductIdWithRank productIdWithRank,
        Map<Long, ProductWithQuantity> productWithQuantities) {
      Long productId = productIdWithRank.productId();
      ProductWithQuantity productWithQuantity = productWithQuantities.get(productId);

      if (productWithQuantity == null) {
        return null;
      }

      Long quantity = productWithQuantity.options().stream()
          .mapToLong(ProductWithQuantityOption::quantity)
          .sum();

      return new ProductWithRank(
          productIdWithRank.rank(),
          quantity,
          productId,
          productWithQuantity.name(),
          productWithQuantity.description(),
          productWithQuantity.basePrice()
      );
    }
  }

  public record ProductIdWithRank(
      Long rank,
      Long productId,
      Long totalSales
  ) {

  }

  public record ProductWithQuantity(
      Long id,
      String name,
      String description,
      Long basePrice,
      List<ProductWithQuantityOption> options
  ) {

    public static ProductWithQuantity from(Product product) {
      List<ProductWithQuantityOption> productWithQuantityOptions = product.getProductOptions()
          .stream()
          .map(productOption -> new ProductWithQuantityOption(
              productOption.getId(),
              productOption.getName(),
              productOption.getDescription(),
              productOption.getAdditionalPrice(),
              productOption.getProductInventory().getQuantity()
          ))
          .toList();

      return new ProductWithQuantity(
          product.getId(),
          product.getName(),
          product.getDescription(),
          product.getBasePrice(),
          productWithQuantityOptions
      );
    }

    public record ProductWithQuantityOption(
        Long id,
        String name,
        String description,
        Long additionalPrice,
        Long quantity
    ) {

    }

  }

  public record ProductInfo(
      Long id,
      String name,
      String description,
      Long basePrice,
      List<ProductOptionInfo> options
  ) {

    public static ProductInfo from(Product product) {
      List<ProductOptionInfo> productOptionInfos = product.getProductOptions().stream()
          .map(ProductOptionInfo::from)
          .toList();

      return new ProductInfo(
          product.getId(),
          product.getName(),
          product.getDescription(),
          product.getBasePrice(),
          productOptionInfos
      );
    }
  }

  public record ProductOptionInfo(
      Long id,
      String name,
      String description,
      Long additionalPrice,
      ProductInventoryInfo productInventoryInfo
  ) {

    public static ProductOptionInfo from(ProductOption productOption) {
      return new ProductOptionInfo(
          productOption.getId(),
          productOption.getName(),
          productOption.getDescription(),
          productOption.getAdditionalPrice(),
          ProductInventoryInfo.from(productOption.getProductInventory())
      );
    }
  }

  public record ProductInventoryInfo(
      Long id,
      Long quantity
  ) {

    public static ProductInventoryInfo from(ProductInventory productInventory) {
      return new ProductInventoryInfo(
          productInventory.getId(),
          productInventory.getQuantity()
      );
    }
  }
}
