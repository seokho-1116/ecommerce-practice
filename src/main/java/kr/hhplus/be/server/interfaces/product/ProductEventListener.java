package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.order.OrderEvent.OrderPaySuccessEvent;
import kr.hhplus.be.server.domain.product.ProductDeductCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductEventListener {

  private final ProductService productService;

  @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
  public void handleProductEvent(OrderPaySuccessEvent event) {
    ProductDeductCommand command = ProductDeductCommand.from(event.productOptionIdAmountMap());
    productService.deductInventory(command);
  }
}