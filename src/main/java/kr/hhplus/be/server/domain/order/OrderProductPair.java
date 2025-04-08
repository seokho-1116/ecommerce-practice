package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderItemOptionCommand;
import kr.hhplus.be.server.domain.product.ProductOption;

public record OrderProductPair(
    UserCoupon userCoupon,
    ProductOption productOption,
    Long amount
) {

  public static List<OrderProductPair> mapping2Pair(
      List<OrderItemOptionCommand> orderItemOptionCommands, List<ProductOption> productOptions,
      List<UserCoupon> userCoupons) {
    Map<Long, ProductOption> productOptionIdMap = productOptions.stream()
        .collect(Collectors.toMap(ProductOption::getId, Function.identity()));

    Map<Long, UserCoupon> userCouponIdMap = userCoupons.stream()
        .collect(Collectors.toMap(UserCoupon::getId, Function.identity()));

    return orderItemOptionCommands.stream()
        .map(orderItemOptionCommand -> {
          ProductOption productOption = productOptionIdMap.get(
              orderItemOptionCommand.productOptionId());
          UserCoupon userCoupon = userCouponIdMap.get(orderItemOptionCommand.userCouponId());

          return new OrderProductPair(userCoupon, productOption,
              orderItemOptionCommand.amount());
        })
        .toList();
  }
}
