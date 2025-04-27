package kr.hhplus.be.server.domain.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.util.ArrayList;
import java.util.List;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderIllegalStateException;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long totalPrice;
  private Long discountPrice;
  private Long finalPrice;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  private Long userId;

  @Version
  private Long version;

  @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<OrderItem> orderItems = new ArrayList<>();

  @Builder
  public Order(Long id, Long totalPrice, Long discountPrice, Long finalPrice, OrderStatus status,
      Long userId) {
    if (totalPrice != null && totalPrice < 0) {
      throw new OrderIllegalStateException("주문 총 금액은 0 이상이어야 합니다.");
    }

    if (discountPrice != null && discountPrice < 0) {
      throw new OrderIllegalStateException("주문 할인 금액은 0 이상이어야 합니다.");
    }

    if (finalPrice != null && finalPrice < 0) {
      throw new OrderIllegalStateException("주문 최종 금액은 0 이상이어야 합니다.");
    }

    if (status == null) {
      throw new OrderIllegalStateException("주문 상태는 필수입니다.");
    }

    this.id = id;
    this.totalPrice = totalPrice;
    this.discountPrice = discountPrice;
    this.finalPrice = finalPrice;
    this.status = status;
    this.userId = userId;
  }

  public static Order newOrder(User user, List<OrderItem> orderItems, UserCoupon userCoupon) {
    Order order = Order.builder()
        .userId(user.getId())
        .status(OrderStatus.CREATED)
        .totalPrice(0L)
        .discountPrice(0L)
        .build();

    for (OrderItem orderItem : orderItems) {
      order.totalPrice += orderItem.getTotalPrice();
      order.setupOrderItem(orderItem);
    }

    if (userCoupon != null) {
      order.discountPrice = userCoupon.calculateDiscountPrice(order.totalPrice);
    }

    order.finalPrice = order.totalPrice - order.discountPrice;

    return order;
  }

  private void setupOrderItem(OrderItem orderItem) {
    this.orderItems.add(orderItem);
    orderItem.setupOrder(this);
  }

  public void pay() {
    if (this.status != OrderStatus.CREATED) {
      throw new OrderIllegalStateException("결제 상태로 변경할 수 없습니다.");
    }

    this.status = OrderStatus.PAID;
  }
}