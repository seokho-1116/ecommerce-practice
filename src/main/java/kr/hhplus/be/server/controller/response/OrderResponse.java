package kr.hhplus.be.server.controller.response;

import java.util.List;
import kr.hhplus.be.server.service.CouponType;
import kr.hhplus.be.server.service.OrderStatus;

public record OrderResponse(
    Long orderId,
    Long userId,
    OrderStatus status,
    Long totalPrice,
    Long discountPrice,
    Long finalPrice,
    List<ItemInfoResponse> itemInfos
) {

  public record ItemInfoResponse(
      Long orderItemId,
      Long productId,
      String productName,
      String productDescription,
      Long productOptionId,
      String productOptionName,
      String productOptionDescription,
      Long basePrice,
      Long additionalPrice,
      Long totalPrice,
      Long discountPrice,
      Long finalPrice,
      Long quantity,
      CouponInfoResponse couponInfo
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
