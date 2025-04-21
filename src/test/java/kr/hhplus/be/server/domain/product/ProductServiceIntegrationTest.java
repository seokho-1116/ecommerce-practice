package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import kr.hhplus.be.server.common.TestHelpRepository;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.order.OrderCommand.ProductAmountPair;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceIntegrationTest {

  @Autowired
  private ProductTestDataGenerator productTestDataGenerator;

  @Autowired
  private ProductService productService;

  @Autowired
  private TestHelpRepository testHelpRepository;

  @BeforeEach
  void setup() {
    Product product = productTestDataGenerator.product();
    testHelpRepository.save(product);

    List<ProductOption> productOption = productTestDataGenerator.productOptions(10);

    for (ProductOption option : productOption) {
      option.setupProduct(product);
      testHelpRepository.save(option);

      ProductInventory productInventory = productTestDataGenerator.productInventory();
      productInventory.setupProductOption(option);
      testHelpRepository.save(productInventory);

      OrderItem orderItem = OrderItem.create(new ProductAmountPair(product, option, 1L));
      TestReflectionUtil.setField(orderItem, "createdAt", LocalDateTime.now().minusDays(2));
      testHelpRepository.save(orderItem);
    }
  }

  @AfterEach
  void tearDown() {
    testHelpRepository.cleanup();
  }

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
