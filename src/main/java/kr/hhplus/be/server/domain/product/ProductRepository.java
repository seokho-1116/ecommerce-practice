package kr.hhplus.be.server.domain.product;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;

public interface ProductRepository {

  List<Product> findAllByProductOptionIds(List<Long> productIds);

  List<ProductInventory> findProductInventoriesByProductOptionIds(List<Long> productOptionIds);

  void saveAll(List<ProductInventory> productInventories);

  List<ProductWithRank> findTop5SellingProductsByBetweenCreatedTsOrderBySellingRanking(
      LocalDateTime from,
      LocalDateTime to
  );

  List<ProductWithQuantity> findAll();
}