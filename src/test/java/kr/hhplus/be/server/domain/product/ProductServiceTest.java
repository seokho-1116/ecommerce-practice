package kr.hhplus.be.server.domain.product;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @DisplayName("상품 재고 차감 커맨드가 null인 경우 상품 비즈니스 예외가 발생해야 한다")
  @Test
  void deductInventory_NullCommand_ThrowsProductBusinessException() {
    // given
    ProductDeductCommand productDeductCommand = null;

    // when & then
    assertThatThrownBy(() -> productService.deductInventory(productDeductCommand))
        .isInstanceOf(ProductBusinessException.class);
  }

  @DisplayName("상품 재고 차감 커맨드가 비어있는 경우 상품 비즈니스 예외가 발생해야 한다")
  @Test
  void deductInventory_EmptyCommand_ThrowsProductBusinessException() {
    // given
    ProductDeductCommand productDeductCommand = new ProductDeductCommand(Map.of());

    // when & then
    assertThatThrownBy(() -> productService.deductInventory(productDeductCommand))
        .isInstanceOf(ProductBusinessException.class);
  }

  @DisplayName("상품 재고 차감 후 차감된 재고를 저장해야 한다")
  @Test
  void deductInventory_Success() {
    // given
    ProductDeductCommand productDeductCommand = new ProductDeductCommand(Map.of(1L, 10L));

    // when
    productService.deductInventory(productDeductCommand);

    // then
    verify(productRepository).saveAll(anyList());
  }
}