package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductOptionIllegalStateException;
import kr.hhplus.be.server.domain.product.ProductOption.ProductOptionBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductOptionTest {

  @DisplayName("상품 옵션 가격이 0보다 크면 상품 옵션이 생성된다")
  @Test
  void createProductWithPositivePriceTest() {
    // given
    long positivePrice = 1000L;
    ProductOptionBuilder productOptionBuilder = ProductOption.builder()
        .id(1L)
        .name("test")
        .additionalPrice(positivePrice)
        .description("test");

    // when
    ProductOption productOption = productOptionBuilder.build();

    // then
    assertThat(productOption.getAdditionalPrice()).isEqualTo(positivePrice);
  }

  @DisplayName("상품 옵션 가격이 0보다 작으면 상품 옵션 상태 예외가 발생한다")
  @Test
  void createProductWithNegativePriceTest() {
    // given
    long negativePrice = -1000L;
    ProductOptionBuilder productOptionBuilder = ProductOption.builder()
        .id(1L)
        .name("test")
        .additionalPrice(negativePrice)
        .description("test");

    // when
    // then
    assertThatThrownBy(productOptionBuilder::build)
        .isInstanceOf(ProductOptionIllegalStateException.class);
  }
}