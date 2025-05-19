package kr.hhplus.be.server.application.payment;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;
import kr.hhplus.be.server.domain.payment.PaymentCommand.PaymentSuccessCommand;
import kr.hhplus.be.server.domain.payment.PaymentDto.PaymentInfo;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPaymentFacade {

  private final PaymentService paymentService;
  private final OrderService orderService;
  private final ProductService productService;
  private final PointService pointService;
  private final PaymentEventPublisher paymentEventPublisher;

  @Transactional
  public PaymentResult payOrder(OrderPaymentCommand orderPaymentCommand) {
    OrderInfo order = orderService.findNotPaidOrderById(orderPaymentCommand.orderId());

    ProductDeductCommand productDeductCommand = ProductDeductCommand.from(order.orderItems());
    productService.deductInventory(productDeductCommand);

    long remainingPoint = pointService.use(orderPaymentCommand.userId(), order.finalPrice());

    OrderInfo orderResult = orderService.pay(order.id());

    PaymentSuccessCommand paymentSuccessCommand = PaymentSuccessCommand.of(
        orderResult.id(),
        orderResult.userId(),
        order.finalPrice()
    );
    PaymentInfo paymentInfo = paymentService.pay(paymentSuccessCommand);

    PaymentSuccessEvent event = PaymentSuccessEvent.from(paymentInfo);
    paymentEventPublisher.publish(event);

    return PaymentResult.of(paymentInfo, remainingPoint);
  }
}