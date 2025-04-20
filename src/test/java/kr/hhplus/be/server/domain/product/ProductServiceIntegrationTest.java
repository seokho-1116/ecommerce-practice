package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@Sql(scripts = "classpath:db/product_test_case.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProductServiceIntegrationTest {

  @Autowired
  private ProductService productService;

  @DisplayName("상위 상품 조회 시 이전 3일동안 가장 많이 팔린 상품이 순위로 정렬되어 조회된다")
  @Test
  void getTopSellingProducts() {
    // given
    // when
    Top5SellingProducts top5SellingProducts = productService.findTop5SellingProducts();

    // then
    assertThat(top5SellingProducts.topSellingProducts())
        .isNotEmpty()
        .isSortedAccordingTo(Comparator.comparing(ProductWithRank::rank));
  }
}
