package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaySuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEvent.OrderSuccessEvent;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisherImpl implements OrderEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void paySuccess(OrderPaySuccessEvent event) {
    applicationEventPublisher.publishEvent(event);
  }

  @Override
  public void orderSuccess(OrderSuccessEvent event) {
    applicationEventPublisher.publishEvent(event);
  }
}
