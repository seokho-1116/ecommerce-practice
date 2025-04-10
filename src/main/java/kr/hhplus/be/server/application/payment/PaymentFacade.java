package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import org.springframework.stereotype.Component;

@Component
public class PaymentFacade {

  private OrderService orderService;
  private ProductService productService;
  private PointService pointService;
  private PaymentEventPublisher paymentEventPublisher;

  public void payment(PaymentCommand paymentCommand) {
    Order order = orderService.findById(paymentCommand.orderId());

    ProductDeductCommand productDeductCommand = ProductDeductCommand.from(order.getOrderItems());
    productService.deductInventory(productDeductCommand);

    pointService.use(paymentCommand.userId(), order.getFinalPrice());

    orderService.pay(order);

    PaymentSuccessEvent event = PaymentSuccessEvent.from(order);
    paymentEventPublisher.publish(event);
  }
}
