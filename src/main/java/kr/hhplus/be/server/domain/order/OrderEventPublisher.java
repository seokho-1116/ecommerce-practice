package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaymentSuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;

public interface OrderEventPublisher {

  void paySuccess(OrderPaymentSuccessEvent event);

  void orderSuccess(OrderSuccessEvent event);
}
