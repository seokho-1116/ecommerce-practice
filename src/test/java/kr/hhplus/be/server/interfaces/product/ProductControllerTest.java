package kr.hhplus.be.server.interfaces.product;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity.ProductWithQuantityOption;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithRank;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ProductService productService;

  @DisplayName("상품 목록 조회 API 테스트")
  @Test
  void findAllProductsTest() throws Exception {
    // given
    ProductWithQuantity productWithQuantity1 = new ProductWithQuantity(
        1L,
        "상품1",
        "상품1 설명",
        1000L,
        List.of(
            new ProductWithQuantityOption(1L, "상품1 옵션1", "상품1 옵션1 설명", 1000L, 1L),
            new ProductWithQuantityOption(2L, "상품1 옵션2", "상품1 옵션2 설명", 2000L, 1L)
        )
    );

    ProductWithQuantity productWithQuantity2 = new ProductWithQuantity(
        2L,
        "상품2",
        "상품2 설명",
        2000L,
        List.of(
            new ProductWithQuantityOption(3L, "상품2 옵션1", "상품2 옵션1 설명", 2000L, 1L),
            new ProductWithQuantityOption(4L, "상품2 옵션2", "상품2 옵션2 설명", 3000L, 1L)
        )
    );

    when(productService.findAllProducts()).thenReturn(
        List.of(productWithQuantity1, productWithQuantity2));

    // when
    // then
    mockMvc.perform(get("/api/v1/products"))
        .andExpect(status().isOk());
  }

  @DisplayName("상위 상품 조회 API 테스트")
  @Test
  void findTopProductsTest() throws Exception {
    // given
    LocalDate now = LocalDate.now();
    Top5SellingProducts top5SellingProducts = new Top5SellingProducts(
        now.atStartOfDay(),
        now.atTime(LocalTime.MAX),
        List.of(
            new ProductWithRank(1L, 5L, 1L, "상품1", "상품1 설명", 1000L),
            new ProductWithRank(2L, 4L, 2L, "상품2", "상품2 설명", 2000L)
        )
    );

    when(productService.findTop5SellingProducts()).thenReturn(top5SellingProducts);

    // when
    // then
    mockMvc.perform(get("/api/v1/products/top-selling"))
        .andExpect(status().isOk());
  }
}