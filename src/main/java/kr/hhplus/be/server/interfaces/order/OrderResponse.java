package kr.hhplus.be.server.interfaces.order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;

public record OrderResponse(

) {

  public record OrderSuccessResponse(
      Long orderId,
      Long userId,
      OrderStatus status,
      Long totalPrice,
      Long discountPrice,
      Long finalPrice,
      CouponInfoResponse couponInfo,
      List<ItemInfoResponse> itemInfos
  ) {

    public static OrderSuccessResponse from(OrderResult orderResult) {
      Order order = orderResult.order();
      UserCoupon userCoupon = orderResult.userCoupon();

      CouponInfoResponse couponInfo = null;
      if (userCoupon != null) {
        couponInfo = new CouponInfoResponse(
            userCoupon.getId(),
            userCoupon.getCoupon().getName(),
            userCoupon.getCoupon().getDiscountRate(),
            userCoupon.getCoupon().getDiscountAmount(),
            userCoupon.getCoupon().getCouponType()
        );
      }

      Map<Long, Long> productOptionIdToAmountMap = orderResult.order().getOrderItems().stream()
          .collect(Collectors.groupingBy(orderItem -> orderItem.getProductOption().getId(),
              Collectors.counting()));

      return new OrderSuccessResponse(
          order.getId(),
          order.getUser().getId(),
          order.getStatus(),
          order.getTotalPrice(),
          order.getDiscountPrice(),
          order.getFinalPrice(),
          couponInfo,
          order.getOrderItems().stream()
              .map(item -> {
                Long amount = productOptionIdToAmountMap.get(item.getProductOption().getId());

                return new ItemInfoResponse(
                    item.getId(),
                    item.getProductName(),
                    item.getProductDescription(),
                    item.getProductOptionName(),
                    item.getProductOptionDescription(),
                    item.getBasePrice(),
                    item.getAdditionalPrice(),
                    item.getTotalPrice(),
                    amount
                );
              })
              .toList()
      );
    }

    public record ItemInfoResponse(
        Long orderItemId,
        String productName,
        String productDescription,
        String productOptionName,
        String productOptionDescription,
        Long basePrice,
        Long additionalPrice,
        Long totalPrice,
        Long quantity
    ) {

    }

    public record CouponInfoResponse(
        Long couponId,
        String couponName,
        Double discountRate,
        Long discountAmount,
        CouponType couponType
    ) {

    }
  }

}
