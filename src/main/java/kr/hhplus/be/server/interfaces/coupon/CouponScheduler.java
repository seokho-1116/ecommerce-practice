package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

  private final CouponService couponService;

  @Scheduled(cron = "* * * * * *", zone = "Asia/Seoul")
  public void issueCoupons() {
    log.info("CouponScheduler - issueCoupons");
    couponService.issueCouponFromQueue();
    log.info("CouponScheduler - issueCoupons - end");
  }
}
