package kr.hhplus.be.server.infrastructure.product;

import kr.hhplus.be.server.domain.product.ProductSellingRankView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSellingRankingViewJpaRepository extends JpaRepository<ProductSellingRankView, Long> {

}
