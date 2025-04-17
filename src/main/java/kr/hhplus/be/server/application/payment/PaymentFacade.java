package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

  private final OrderService orderService;
  private final ProductService productService;
  private final PointService pointService;
  private final PaymentEventPublisher paymentEventPublisher;

  public PaymentResult payOrder(PaymentCommand paymentCommand) {
    Order order = orderService.findNotPaidOrderById(paymentCommand.orderId());

    ProductDeductCommand productDeductCommand = ProductDeductCommand.from(order.getOrderItems());
    productService.deductInventory(productDeductCommand);

    long remainingPoint = pointService.use(paymentCommand.userId(), order.getFinalPrice());

    orderService.pay(order);

    PaymentSuccessEvent event = PaymentSuccessEvent.from(order);
    paymentEventPublisher.publish(event);

    return PaymentResult.of(order, remainingPoint);
  }
}
