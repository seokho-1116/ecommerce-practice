package kr.hhplus.be.server.infrastructure.product;

import java.util.List;
import kr.hhplus.be.server.domain.product.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductInventoryJpaRepository extends JpaRepository<ProductInventory, Long> {

  List<ProductInventory> findProductInventoriesByProductOptionIdIn(List<Long> productOptionIds);
}
