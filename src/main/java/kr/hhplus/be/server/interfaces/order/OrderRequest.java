package kr.hhplus.be.server.interfaces.order;

import java.util.List;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.Item;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.ProductIdItemPair;

public record OrderRequest(
    Long userId,
    Long userCouponId,
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
      Long productId,
      Long productOptionId,
      Long amount
  ) {

  }
}
