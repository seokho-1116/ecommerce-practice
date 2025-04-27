package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductIdWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;

public interface ProductRepository {

  List<Product> findAllByProductOptionIds(List<Long> productIds);

  List<ProductInventory> findProductInventoriesForUpdateByProductOptionIds(List<Long> productOptionIds);

  void saveAll(List<ProductInventory> productInventories);
  
  List<ProductIdWithRank> findTop5SellingProducts(
      LocalDateTime from,
      LocalDateTime to
  );

  List<ProductIdWithRank> findTop5SellingProductsFromRankView(
      LocalDateTime from,
      LocalDateTime to
  );

  List<ProductWithQuantity> findAll();

  List<Product> findAllByIdIn(List<Long> productIds);

  void saveAllRankingViews(List<ProductSellingRankView> productSellingRankViews);
}