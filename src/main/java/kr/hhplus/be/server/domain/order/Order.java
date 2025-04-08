package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import kr.hhplus.be.server.domain.BaseEntity;
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
  private OrderStatus status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public Order(Long id, Long totalPrice, Long discountPrice, Long finalPrice, OrderStatus status,
      User user) {
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
    this.user = user;
  }

  public static Order newOrder(User user, List<OrderItem> orderItems) {
    Order order = Order.builder()
        .user(user)
        .status(OrderStatus.CREATED)
        .totalPrice(0L)
        .discountPrice(0L)
        .finalPrice(0L)
        .build();

    for (OrderItem orderItem : orderItems) {
      order.totalPrice += orderItem.getTotalPrice();
      order.discountPrice += orderItem.getDiscountPrice();
      order.finalPrice += orderItem.getFinalPrice();

      orderItem.setupOrder(order);
    }

    return order;
  }
}