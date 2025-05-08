package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductScheduler {

  private final ProductService productService;

  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void calculateProductSellingRanking() {
    productService.saveTop5SellingProducts();
    productService.saveTop5SellingProductIdsInCache();
  }
}
