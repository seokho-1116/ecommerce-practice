package kr.hhplus.be.server.interfaces.product;

import java.util.List;
import kr.hhplus.be.server.domain.product.ProductDto.ProductWithQuantity;
import kr.hhplus.be.server.domain.product.ProductDto.Top5SellingProducts;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.product.ProductResponse.ProductSummaryResponse;
import kr.hhplus.be.server.interfaces.product.ProductResponse.TopSellingProductsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController implements ProductControllerSpec {

  private final ProductService productService;

  @GetMapping
  public CommonResponseWrapper<List<ProductSummaryResponse>> findAllProducts() {
    List<ProductWithQuantity> productWithQuantities = productService.findAllProducts();

    List<ProductSummaryResponse> responses = ProductSummaryResponse.from(productWithQuantities);

    return CommonResponseWrapper.ok(responses);
  }

  @GetMapping("/top-selling")
  public CommonResponseWrapper<TopSellingProductsResponse> findTopSellingProducts() {
    Top5SellingProducts topSellingProducts = productService.findTop5SellingProducts();

    TopSellingProductsResponse response = TopSellingProductsResponse.from(topSellingProducts);

    return CommonResponseWrapper.ok(response);
  }
}
