package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.support.CacheKey;

public interface ProductRepository {

  List<Product> findAllByProductOptionIds(List<Long> productIds);

  List<ProductInventory> findProductInventoriesForUpdateByProductOptionIds(
      List<Long> productOptionIds);

  void saveAll(List<ProductInventory> productInventories);

  List<ProductIdWithRank> findAllSellingProductsWithRank(
      LocalDateTime from,
      LocalDateTime to
  );

  List<ProductIdWithRank> findTop5SellingProductsFromRankView(
      LocalDateTime from,
      LocalDateTime to
  );


  List<ProductIdWithRank> findTop5SellingProductsFromRankViewInCache(
      LocalDateTime from,
      LocalDateTime to
  );

  List<ProductWithQuantity> findAll();

  List<Product> findAllByIdIn(List<Long> productIds);

  void saveAllRankingViews(List<ProductSellingRankView> productSellingRankViews);

  void saveTop5SellingProductInCache(CacheKey key, List<ProductIdWithRank> productIdWithRanks, long ttl, TimeUnit timeUnit);
}