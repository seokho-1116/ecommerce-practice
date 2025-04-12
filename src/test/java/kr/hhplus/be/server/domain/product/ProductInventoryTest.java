package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductInventoryIllegalStateException;
import kr.hhplus.be.server.domain.product.ProductInventory.ProductInventoryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductInventoryTest {

  @DisplayName("상품 재고가 요청 재고보다 작으면 상품 재고 예외가 발생한다")
  @Test
  void createProductWithNegativeInventoryTest() {
    // given
    ProductInventory productInventory = ProductInventory.builder()
        .id(1L)
        .quantity(1L)
        .build();
    long requestQuantity = 2L;

    // when
    // then
    assertThatThrownBy(() -> productInventory.deduct(requestQuantity))
        .isInstanceOf(ProductInventoryIllegalStateException.class);
  }

  @DisplayName("상품 재고가 요청 재고보다 크면 상품 재고가 차감된다")
  @Test
  void createProductInventoryTest() {
    //given
    ProductInventory productInventory = ProductInventory.builder()
        .id(1L)
        .quantity(2L)
        .build();
    long requestQuantity = 1L;

    //when
    productInventory.deduct(requestQuantity);

    //then
    assertThat(productInventory.getQuantity()).isEqualTo(1L);
  }

  @DisplayName("상품 재고는 0보다 작을 수 없다")
  @Test
  void createProductInventoryWithNegativeQuantityTest() {
    //given
    ProductInventoryBuilder productInventoryBuilder = ProductInventory.builder()
        .id(1L)
        .quantity(-1L);

    //when
    //then
    assertThatThrownBy(productInventoryBuilder::build)
        .isInstanceOf(ProductInventoryIllegalStateException.class);
  }

  @DisplayName("상품 재고는 0일 수 있다")
  @Test
  void createProductInventoryWithZeroQuantityTest() {
    //given
    ProductInventoryBuilder productInventoryBuilder = ProductInventory.builder()
        .id(1L)
        .quantity(0L);

    //when
    ProductInventory productInventory = productInventoryBuilder.build();

    //then
    assertThat(productInventory.getQuantity()).isZero();
  }
}