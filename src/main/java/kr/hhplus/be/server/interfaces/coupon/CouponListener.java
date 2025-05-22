package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderEvent.UseCouponEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponListener {

  private final CouponService couponService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleUseCouponEvent(UseCouponEvent event) {
    couponService.use(event.userCouponId());
  }
}
