package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductScheduler {

  private final ProductService productService;

  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void calculateProductSellingRanking() {
    log.info("ProductScheduler - calculateProductSellingRanking");
    productService.saveAllSellingProducts();
    log.info("ProductScheduler - calculateProductSellingRanking - end");
  }
}
