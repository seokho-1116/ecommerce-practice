package kr.hhplus.be.server.controller;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.ProductSummaryResponse;
import kr.hhplus.be.server.controller.response.TopSellingProductsResponse;
import kr.hhplus.be.server.controller.response.TopSellingProductsResponse.TopSellingProductResponse;
import kr.hhplus.be.server.controller.spec.ProductControllerSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController implements ProductControllerSpec {

  @GetMapping
  public CommonResponseWrapper<List<ProductSummaryResponse>> findAllProducts() {
    return CommonResponseWrapper.ok(
        List.of(
            new ProductSummaryResponse(
                1L,
                "상품1",
                "상품1 설명",
                1000L,
                List.of(
                    new ProductSummaryResponse.SummaryOptionResponse(
                        1L,
                        "옵션1",
                        "옵션1 설명",
                        100L,
                        10L
                    ),
                    new ProductSummaryResponse.SummaryOptionResponse(
                        2L,
                        "옵션2",
                        "옵션2 설명",
                        200L,
                        20L
                    )
                )
            ),
            new ProductSummaryResponse(
                2L,
                "상품2",
                "상품2 설명",
                2000L,
                List.of(
                    new ProductSummaryResponse.SummaryOptionResponse(
                        3L,
                        "옵션3",
                        "옵션3 설명",
                        300L,
                        30L
                    ),
                    new ProductSummaryResponse.SummaryOptionResponse(
                        4L,
                        "옵션4",
                        "옵션4 설명",
                        400L,
                        40L
                    )
                )
            )
        )
    );
  }

  @GetMapping("/top-selling")
  public CommonResponseWrapper<TopSellingProductsResponse> findTopSellingProducts() {
    return CommonResponseWrapper.ok(
        new TopSellingProductsResponse(
            LocalDateTime.now(),
            LocalDateTime.now().minusDays(4),
            List.of(
                new TopSellingProductResponse(
                    1L,
                    1L,
                    1L,
                    "상품1",
                    "상품1 설명",
                    1000L,
                    100L,
                    10L
                )
            )
        )
    );
  }
}
