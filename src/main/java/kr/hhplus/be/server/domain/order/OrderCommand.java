package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderCommandIllegalStateException;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductOption;
import kr.hhplus.be.server.domain.user.User;

public record OrderCommand(
    User user,
    List<ProductAmountPair> productAmountPairs,
    UserCoupon userCoupon
) {

  public OrderCommand {
    if (user == null) {
      throw new OrderCommandIllegalStateException("주문자 정보는 필수입니다.");
    }

    if (productAmountPairs == null || productAmountPairs.isEmpty()) {
      throw new OrderCommandIllegalStateException("주문할 상품은 필수입니다.");
    }
  }

  public record ProductAmountPair(
      Product product,
      ProductOption productOption,
      Long amount
  ) {

    public ProductAmountPair {
      if (product == null) {
        throw new OrderCommandIllegalStateException("상품은 필수입니다.");
      }

      if (productOption == null) {
        throw new OrderCommandIllegalStateException("상품 옵션은 필수입니다.");
      }

      if (amount == null || amount <= 0) {
        throw new OrderCommandIllegalStateException("상품 수량은 1 이상이어야 합니다.");
      }
    }

  }

  public record OrderCreateCommand(
      Long userId,
      Long userCouponId,
      List<ProductIdItemPair> productIdItemPairs
  ) {

    public OrderCreateCommand {
      if (userId == null) {
        throw new OrderCommandIllegalStateException("주문자 ID는 필수입니다.");
      }

      if (productIdItemPairs == null || productIdItemPairs.isEmpty()) {
        throw new OrderCommandIllegalStateException("주문할 상품은 필수입니다.");
      }
    }

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

      public ProductIdItemPair {
        if (productId == null) {
          throw new OrderCommandIllegalStateException("상품 ID는 필수입니다.");
        }

        if (item == null) {
          throw new OrderCommandIllegalStateException("상품 옵션은 필수입니다.");
        }
      }
    }

    public record Item(
        Long productOptionId,
        Long amount
    ) {

      public Item {
        if (productOptionId == null) {
          throw new OrderCommandIllegalStateException("상품 옵션 ID는 필수입니다.");
        }

        if (amount == null || amount <= 0) {
          throw new OrderCommandIllegalStateException("상품 수량은 1 이상이어야 합니다.");
        }
      }
    }
  }
}
