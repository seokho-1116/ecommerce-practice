package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaySuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;

public interface OrderEventPublisher {

  void paySuccess(OrderPaySuccessEvent event);

  void orderSuccess(OrderSuccessEvent event);
}
