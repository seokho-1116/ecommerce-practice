package kr.hhplus.be.server.interfaces.product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    LocalTime now = LocalTime.now();
    int oneHourAgo = now.getHour() - 1;
    LocalDateTime from = LocalDate.now().atTime(oneHourAgo, 0, 0, 0);
    LocalDateTime to = LocalDate.now().atTime(now.getHour(), 0, 0, 0);

    productService.saveTop5SellingProductsBefore(from, to);
  }
}
