package kr.hhplus.be.server.infrastructure.product;

import java.util.List;
import kr.hhplus.be.server.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p FROM Product p JOIN FETCH p.productOptions po JOIN FETCH po.productInventory WHERE p.id IN :productIds")
  List<Product> findAllFetchedByIdIn(List<Long> productIds);

  @Query("SELECT p FROM Product p JOIN FETCH p.productOptions po JOIN FETCH po.productInventory WHERE po.id IN :productOptionIds")
  List<Product> findAllByProductOptionsIdIn(List<Long> productOptionIds);

  @Query("SELECT p FROM Product p JOIN FETCH p.productOptions po JOIN FETCH po.productInventory")
  List<Product> findAllFetched();
}
