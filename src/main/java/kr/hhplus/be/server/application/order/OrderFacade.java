package kr.hhplus.be.server.application.order;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.User;
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

  public OrderResult createOrder(OrderCreateCommand orderCreateCommand) {
    List<Product> products = productService.findAllByProductOptionIds(
        orderCreateCommand.productOptionIds());

    User user = userService.findUserById(orderCreateCommand.userId());
    UserCoupon userCoupon = couponService.findUserCouponByUserCouponId(
        orderCreateCommand.userCouponId());

    OrderCommand orderCommand = orderCreateCommand.toOrderCommand(products, user, userCoupon);
    Order order = orderService.createOrder(orderCommand);

    couponService.use(userCoupon);

    return OrderResult.of(order, userCoupon);
  }
}
