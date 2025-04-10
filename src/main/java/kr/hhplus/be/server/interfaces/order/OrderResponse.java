package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
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
      @Schema(description = "주문 ID")
      Long orderId,

      @Schema(description = "유저 ID")
      Long userId,

      @Schema(description = "주문 상태")
      OrderStatus status,

      @Schema(description = "주문 총 가격")
      Long totalPrice,

      @Schema(description = "주문 할인 가격")
      Long discountPrice,

      @Schema(description = "주문 최종 가격")
      Long finalPrice,

      @Schema(description = "쿠폰 정보")
      CouponInfoResponse couponInfo,

      @Schema(description = "주문 상품 목록")
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
        @Schema(description = "주문 상품 ID")
        Long orderItemId,

        @Schema(description = "상품 이름")
        String productName,

        @Schema(description = "상품 설명")
        String productDescription,

        @Schema(description = "상품 옵션 이름")
        String productOptionName,

        @Schema(description = "상품 옵션 설명")
        String productOptionDescription,

        @Schema(description = "상품 기본 가격")
        Long basePrice,

        @Schema(description = "상품 옵션 추가 가격")
        Long additionalPrice,

        @Schema(description = "상품 총 가격")
        Long totalPrice,

        @Schema(description = "상품 수량")
        Long quantity
    ) {

    }

    public record CouponInfoResponse(
        @Schema(description = "쿠폰 ID")
        Long couponId,

        @Schema(description = "쿠폰 이름")
        String couponName,

        @Schema(description = "쿠폰 할인율")
        Double discountRate,

        @Schema(description = "쿠폰 할인 금액")
        Long discountAmount,

        @Schema(description = "쿠폰 타입")
        CouponType couponType
    ) {

    }
  }

}
