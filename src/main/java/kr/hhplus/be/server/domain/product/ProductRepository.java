package kr.hhplus.be.server.domain.product;

import java.util.List;

public interface ProductRepository {

  List<Product> findAllByProductOptionIds(List<Long> productIds);

  List<ProductInventory> findProductInventoriesByProductOptionIds(List<Long> productOptionIds);

  void saveAll(List<ProductInventory> productInventories);
}