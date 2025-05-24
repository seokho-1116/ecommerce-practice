package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaySuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

  private final PointService pointService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handlePointEvent(OrderPaySuccessEvent event) {
    pointService.charge(event.userId(), event.finalPrice());
  }
}
