package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEvent.UseCouponEvent;

public interface OrderEventPublisher {

  void success(OrderSuccessEvent event);

  void useCoupon(UseCouponEvent event);
}
