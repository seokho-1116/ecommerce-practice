package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderDto.OrderItemInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
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
      OrderInfo order = orderResult.order();
      UserCouponInfo userCoupon = orderResult.userCoupon();

      CouponInfoResponse couponInfo = null;
      if (userCoupon != null) {
        couponInfo = new CouponInfoResponse(
            userCoupon.id(),
            userCoupon.couponName(),
            userCoupon.discountRate(),
            userCoupon.discountAmount(),
            userCoupon.couponType()
        );
      }

      Map<Long, Long> productOptionIdToAmountMap = orderResult.order().orderItems().stream()
          .collect(Collectors.groupingBy(OrderItemInfo::productOptionId, Collectors.counting()));

      return new OrderSuccessResponse(
          order.id(),
          order.userId(),
          order.status(),
          order.totalPrice(),
          order.discountPrice(),
          order.finalPrice(),
          couponInfo,
          order.orderItems().stream()
              .map(item -> {
                Long amount = productOptionIdToAmountMap.get(item.productOptionId());

                return new ItemInfoResponse(
                    item.id(),
                    item.productName(),
                    item.productDescription(),
                    item.productOptionName(),
                    item.productOptionDescription(),
                    item.basePrice(),
                    item.additionalPrice(),
                    item.totalPrice(),
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
