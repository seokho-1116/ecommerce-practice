package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaymentSuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponEventListener {

  private final CouponService couponService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleOrderSuccessEvent(OrderSuccessEvent event) {
    couponService.reserveCouponForOrder(event.userCouponId(), event.orderId());
  }

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleOrderPaymentSuccessEvent(OrderPaymentSuccessEvent event) {
    couponService.use(event.userId(), event.orderId());
  }
}
