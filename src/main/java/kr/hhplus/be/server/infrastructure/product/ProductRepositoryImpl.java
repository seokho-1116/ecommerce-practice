package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity.ProductWithQuantityOption;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository productJpaRepository;
  private final ProductCustomRepository productCustomRepository;
  private final ProductInventoryJpaRepository productInventoryJpaRepository;

  @Override
  public List<Product> findAllByProductOptionIds(List<Long> productIds) {
    return productJpaRepository.findAllByProductOptionsIdIn(productIds);
  }

  @Override
  public List<ProductInventory> findProductInventoriesByProductOptionIds(
      List<Long> productOptionIds) {
    return productInventoryJpaRepository.findProductInventoriesByProductOptionIdIn(productOptionIds);
  }

  @Override
  public void saveAll(List<ProductInventory> productInventories) {
    productInventoryJpaRepository.saveAll(productInventories);
  }

  @Override
  public List<ProductWithRank> findTop5SellingProductsByBetweenCreatedTsOrderBySellingRanking(
      LocalDateTime from, LocalDateTime to) {
    List<ProductIdWithRank> productIdWithRanks = productCustomRepository.findTop5SellingProductIdsByBetweenCreatedTsOrderByAmount(
        from, to);

    List<Long> productIds = productIdWithRanks.stream()
        .map(ProductIdWithRank::productId)
        .toList();
    List<Product> products = productJpaRepository.findAllFetchedByIdIn(productIds);

    Map<Long, ProductWithQuantity> productWithQuantities = products.stream()
        .map(ProductWithQuantity::from)
        .collect(Collectors.toMap(ProductWithQuantity::id, Function.identity()));

    return productIdWithRanks.stream()
        .map(productIdWithRank -> {
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
        })
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public List<ProductWithQuantity> findAll() {
    List<Product> products = productJpaRepository.findAllFetched();

    return products.stream()
        .map(ProductWithQuantity::from)
        .toList();
  }
}
