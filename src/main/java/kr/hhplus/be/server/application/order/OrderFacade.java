package kr.hhplus.be.server.application.order;

import jakarta.transaction.Transactional;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponDto.UserCouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderDto.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductDto.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserDto.UserInfo;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacade {

  private final OrderService orderService;
  private final ProductService productService;
  private final CouponService couponService;
  private final UserService userService;

  @Transactional
  public OrderResult createOrder(OrderCreateCommand orderCreateCommand) {
    List<ProductInfo> products = productService.findAllByProductOptionIds(
        orderCreateCommand.productOptionIds());

    UserInfo user = userService.findUserInfoById(orderCreateCommand.userId());
    UserCouponInfo userCoupon = couponService.findUserCouponByUserCouponId(
        orderCreateCommand.userCouponId());

    OrderCommand orderCommand = orderCreateCommand.toOrderCommand(products, user, userCoupon);
    OrderInfo order = orderService.createOrder(orderCommand);

    couponService.use(userCoupon.id());

    return OrderResult.of(order, userCoupon);
  }
}
