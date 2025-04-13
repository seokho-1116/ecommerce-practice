package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
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
    return productCustomRepository.findTop5SellingProductsByBetweenCreatedTsOrderBySellingPrice(from, to);
  }

  @Override
  public List<ProductWithQuantity> findAll() {
    List<Product> products = productJpaRepository.findAll();

    return products.stream()
        .map(ProductWithQuantity::from)
        .toList();
  }
}
