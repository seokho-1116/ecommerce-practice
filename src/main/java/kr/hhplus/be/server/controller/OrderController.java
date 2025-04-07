package kr.hhplus.be.server.controller;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.OrderPaymentRequest;
import kr.hhplus.be.server.controller.response.OrderPaymentResponse;
import kr.hhplus.be.server.controller.response.OrderRequest;
import kr.hhplus.be.server.controller.response.OrderResponse;
import kr.hhplus.be.server.controller.response.OrderResponse.CouponInfoResponse;
import kr.hhplus.be.server.controller.response.OrderResponse.ItemInfoResponse;
import kr.hhplus.be.server.controller.spec.OrderControllerSpec;
import kr.hhplus.be.server.domain.coupon.CouponType;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerSpec {

  @PostMapping
  public CommonResponseWrapper<OrderResponse> createOrder(@RequestBody OrderRequest request) {
    return CommonResponseWrapper.ok(
        new OrderResponse(
            1L,
            1L,
            OrderStatus.CREATED,
            1000L,
            1000L,
            1000L,
            List.of(
                new ItemInfoResponse(
                    1L,
                    1L,
                    "상품 이름",
                    "상품 설명",
                    1L,
                    "상품 옵션 이름",
                    "상품 옵션 설명",
                    1000L,
                    1000L,
                    2000L,
                    1000L,
                    1000L,
                    5L,
                    new CouponInfoResponse(
                        1L,
                        "쿠폰 이름",
                        0.1,
                        null,
                        CouponType.PERCENTAGE
                    )
                )
            )
        )
    );
  }

  @PostMapping("/{orderId}/payments")
  public CommonResponseWrapper<OrderPaymentResponse> paymentOrder(
      @PathVariable long orderId,
      @RequestBody OrderPaymentRequest request) {
    return CommonResponseWrapper.ok(
        new OrderPaymentResponse(
            1L,
            1L,
            5L,
            OrderStatus.PAID,
            1000L,
            LocalDateTime.now()
        )
    );
  }
}
