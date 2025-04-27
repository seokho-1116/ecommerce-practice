package kr.hhplus.be.server.infrastructure.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductSellingRankView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository productJpaRepository;
  private final ProductCustomRepository productCustomRepository;
  private final ProductInventoryJpaRepository productInventoryJpaRepository;
  private final ProductSellingRankingViewJpaRepository productSellingRankingViewJpaRepository;

  @Override
  public List<Product> findAllByProductOptionIds(List<Long> productIds) {
    return productJpaRepository.findAllByProductOptionsIdIn(productIds);
  }

  @Override
  public List<ProductInventory> findProductInventoriesForUpdateByProductOptionIds(
      List<Long> productOptionIds) {
    return productInventoryJpaRepository.findProductInventoriesForUpdateByProductOptionIdIn(
        productOptionIds);
  }

  @Override
  public void saveAll(List<ProductInventory> productInventories) {
    productInventoryJpaRepository.saveAll(productInventories);
  }

  @Override
  public List<ProductIdWithRank> findTop5SellingProducts(
      LocalDateTime from, LocalDateTime to) {
    return productCustomRepository.findTop5SellingProducts(
        from, to);
  }

  @Override
  public List<ProductIdWithRank> findTop5SellingProductsFromRankView(
      LocalDateTime from, LocalDateTime to) {
    return productCustomRepository.findTop5SellingProductsFromRankView(
        from, to);
  }

  @Override
  public List<ProductWithQuantity> findAll() {
    List<Product> products = productJpaRepository.findAllFetched();

    return products.stream()
        .map(ProductWithQuantity::from)
        .toList();
  }

  @Override
  public List<Product> findAllByIdIn(List<Long> productIds) {
    return productJpaRepository.findAllFetchedByIdIn(productIds);
  }

  @Override
  public void saveAllRankingViews(List<ProductSellingRankView> productSellingRankViews) {
    productSellingRankingViewJpaRepository.saveAll(productSellingRankViews);
  }
}
