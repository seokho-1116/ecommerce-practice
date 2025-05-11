package kr.hhplus.be.server.infrastructure.product;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

public interface ProductInventoryJpaRepository extends JpaRepository<ProductInventory, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
  List<ProductInventory> findProductInventoriesForUpdateByProductOptionIdIn(
      List<Long> productOptionIds);
}
