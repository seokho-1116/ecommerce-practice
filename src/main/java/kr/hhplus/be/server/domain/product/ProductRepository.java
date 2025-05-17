package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithTotalSales;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.support.CacheKeyHolder;
import org.springframework.data.util.Pair;

public interface ProductRepository {

  List<Product> findAllByProductOptionIds(List<Long> productIds);

  List<ProductInventory> findProductInventoriesForUpdateByProductOptionIds(
      List<Long> productOptionIds);

  void saveAll(List<ProductInventory> productInventories);

  List<ProductIdWithTotalSales> findAllSellingProductsWithRank(
      LocalDateTime from,
      LocalDateTime to
  );

  List<Pair<Long, Long>> findAllTopSellingProducts(CacheKeyHolder<String> key, long startInclusive, long endExclusive);

  List<ProductWithQuantity> findAll();

  List<Product> findAllByIdIn(List<Long> productIds);

  void saveAllRankingViews(List<ProductSellingRankView> productSellingRankViews);

  void saveInRankingBoard(Long productId, Long totalSales, CacheKeyHolder<String> key);
}