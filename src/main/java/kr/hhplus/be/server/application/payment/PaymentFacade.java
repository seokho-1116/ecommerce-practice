package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

  private final OrderService orderService;
  private final ProductService productService;
  private final PointService pointService;
  private final PaymentEventPublisher paymentEventPublisher;
  private final TransactionTemplate transactionTemplate;

  public PaymentResult payOrder(PaymentCommand paymentCommand) {
    PaymentResult result = transactionTemplate.execute(status -> {
      OrderInfo order = orderService.findNotPaidOrderById(paymentCommand.orderId());

      ProductDeductCommand productDeductCommand = ProductDeductCommand.from(order.orderItems());
      productService.deductInventory(productDeductCommand);

      long remainingPoint = pointService.use(paymentCommand.userId(), order.finalPrice());

      OrderInfo orderResult = orderService.pay(order.id());
      return PaymentResult.of(orderResult, remainingPoint);
    });

    PaymentSuccessEvent event = PaymentSuccessEvent.from(result.order());
    paymentEventPublisher.publish(event);

    return result;
  }
}
