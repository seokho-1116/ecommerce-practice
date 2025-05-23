package kr.hhplus.be.server.infrastructure.product;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithTotalSales;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductInventory;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductSellingRankView;
import kr.hhplus.be.server.infrastructure.support.RedisRepository;
import kr.hhplus.be.server.support.CacheKeyHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

  private final ProductJpaRepository productJpaRepository;
  private final ProductCustomRepository productCustomRepository;
  private final ProductInventoryJpaRepository productInventoryJpaRepository;
  private final ProductSellingRankingViewJpaRepository productSellingRankingViewJpaRepository;
  private final RedisRepository redisRepository;

  @Override
  public List<Product> findAllByProductOptionIds(List<Long> productIds) {
    return productJpaRepository.findAllByProductOptionsIdIn(productIds);
  }

  @Override
  public List<ProductInventory> findProductInventoriesForUpdateByProductOptionIds(
      List<Long> productOptionIds) {
    List<Long> sorted = productOptionIds.stream()
        .sorted()
        .toList();

    return productInventoryJpaRepository.findProductInventoriesForUpdateByProductOptionIdIn(sorted);
  }

  @Override
  public void saveAll(List<ProductInventory> productInventories) {
    productInventoryJpaRepository.saveAll(productInventories);
  }

  @Override
  public List<ProductIdWithTotalSales> findAllSellingProductsWithRank(
      LocalDateTime from, LocalDateTime to) {
    return productCustomRepository.findAllSellingProductsWithRank(
        from, to);
  }

  @Override
  public List<Pair<Long, Long>> findAllTopSellingProducts(CacheKeyHolder<String> key, long startInclusive,
      long endExclusive) {
    long start = startInclusive < 0 ? 0 : startInclusive;
    long end = endExclusive < 0 ? 0 : endExclusive - 1;
    return redisRepository.findReverseRangeInZsetWithRank(key.generate(), start, end,
        new TypeReference<>() {
        });
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

  @Override
  public void saveInRankingBoard(Long productId, Long totalSales, CacheKeyHolder<String> key) {
    redisRepository.upsertScoreInZset(
        key.generate(),
        String.valueOf(productId),
        totalSales
    );
  }
}
