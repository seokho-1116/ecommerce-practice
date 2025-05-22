package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;

public interface OrderEventPublisher {

  void success(OrderSuccessEvent event);
}
