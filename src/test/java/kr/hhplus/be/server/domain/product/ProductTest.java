package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.hhplus.be.server.domain.product.Product.ProductBuilder;
import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductIllegalStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

  @DisplayName("상품 가격이 0보다 크면 상품이 생성된다")
  @Test
  void createProductWithPositivePriceTest() {
    // given
    long positivePrice = 1000L;
    ProductBuilder productBuilder = Product.builder()
        .id(1L)
        .name("test")
        .basePrice(positivePrice)
        .description("test");

    // when
    Product product = productBuilder.build();

    // then
    assertThat(product.getBasePrice()).isEqualTo(positivePrice);
  }

  @DisplayName("상품 가격이 0보다 작으면 상품 상태 예외가 발생한다")
  @Test
  void createProductWithNegativePriceTest() {
    // given
    long negativePrice = -1000L;
    ProductBuilder productBuilder = Product.builder()
        .id(1L)
        .name("test")
        .basePrice(negativePrice)
        .description("test");

    // when
    // then
    assertThatThrownBy(productBuilder::build)
        .isInstanceOf(ProductIllegalStateException.class);
  }
}