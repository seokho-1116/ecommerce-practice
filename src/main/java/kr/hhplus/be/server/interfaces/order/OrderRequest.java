package kr.hhplus.be.server.interfaces.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.Item;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.ProductIdItemPair;
import kr.hhplus.be.server.domain.payment.PaymentCommand.OrderPaymentCommand;

public record OrderRequest(
    @NotNull
    Long userId,

    Long userCouponId,

    @NotEmpty
    List<AmountProductOptionRequest> amountProductOptions
) {

  public OrderCreateCommand toCreateCommand() {
    List<ProductIdItemPair> productItemPairs = amountProductOptions.stream()
        .map(option -> new ProductIdItemPair(
            option.productId(),
            new Item(
                option.productOptionId(),
                option.amount()
            )
        ))
        .toList();

    return new OrderCreateCommand(
        userId,
        userCouponId,
        productItemPairs
    );
  }

  public record AmountProductOptionRequest(
      @NotNull
      Long productId,

      @NotNull
      Long productOptionId,

      @NotNull
      Long amount
  ) {

  }

  public record OrderPaymentRequest(
      @NotNull
      Long userId
  ) {

    public OrderPaymentCommand toCommand(long orderId) {
      return new OrderPaymentCommand(
          userId,
          orderId
      );
    }
  }
}
