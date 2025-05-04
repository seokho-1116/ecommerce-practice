package kr.hhplus.be.server.application.payment;

import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.support.LockCommand;
import kr.hhplus.be.server.domain.support.LockTemplate;
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
  private final LockTemplate lockTemplate;

  public PaymentResult payOrder(PaymentCommand paymentCommand) {
    Order order = orderService.findNotPaidOrderById(paymentCommand.orderId());
    ProductDeductCommand productDeductCommand = ProductDeductCommand.from(order.getOrderItems());

    PaymentResult result = lockTemplate.execute(() ->
        transactionTemplate.execute(status -> {
          productService.deductInventory(productDeductCommand);

          long remainingPoint = pointService.use(paymentCommand.userId(), order.getFinalPrice());

          Order payedOrder = orderService.pay(order.getId());
          return PaymentResult.of(payedOrder, remainingPoint);
        }), LockCommand.of("product:stock", 30, TimeUnit.SECONDS, productDeductCommand.toKeys())
    );

    PaymentSuccessEvent event = PaymentSuccessEvent.from(result.order());
    paymentEventPublisher.publish(event);

    return result;
  }
}