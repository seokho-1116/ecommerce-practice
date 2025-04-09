package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand.ProductIdItemPair;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.user.User;

public record OrderCommand(
    User user,
    List<ProductAmountPair> productAmountPairs,
    UserCoupon userCoupon
) {

  public record ProductAmountPair(
      Product product,
      ProductOption productOption,
      Long amount
  ) {

  }

  public record OrderCreateCommand(
      Long userId,
      Long userCouponId,
      List<ProductIdItemPair> productIdItemPairs
  ) {

    public List<Long> productOptionIds() {
      return productIdItemPairs.stream()
          .map(ProductIdItemPair::productId)
          .toList();
    }

    public OrderCommand toOrderCommand(List<Product> products, User user, UserCoupon userCoupon) {
      Map<Long, Product> productIdMap = products.stream()
          .collect(Collectors.toMap(Product::getId, Function.identity()));

      Map<Long, ProductOption> productOptionIdMap = products.stream()
          .flatMap(product -> product.getProductOptions().stream())
          .collect(Collectors.toMap(ProductOption::getId, Function.identity()));

      List<ProductAmountPair> productAmountPairs = productIdItemPairs.stream()
          .map(productIdItemPair -> {
            Product product = productIdMap.get(productIdItemPair.productId());

            ProductOption productOption = productOptionIdMap.get(
                productIdItemPair.item().productOptionId());

            return new ProductAmountPair(product, productOption,
                productIdItemPair.item().amount());
          })
          .toList();

      return new OrderCommand(user, productAmountPairs, userCoupon);
    }

    public record ProductIdItemPair(
        Long productId,
        Item item
    ) {

    }

    public record Item(
        Long productOptionId,
        Long amount
    ) {

    }
  }
}
