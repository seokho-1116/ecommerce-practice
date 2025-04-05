package kr.hhplus.be.server.controller.response;

import java.util.List;

public record OrderRequest(
    Long userId,
    List<AmountProductOptionRequest> amountProductOptions
) {

  public record AmountProductOptionRequest(
      Long productOptionId,
      Long couponId,
      Long amount
  ) {

  }
}
