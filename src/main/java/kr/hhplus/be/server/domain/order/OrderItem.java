package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.order.OrderBusinessException.OrderItemIllegalStateException;
import kr.hhplus.be.server.domain.order.OrderCommand.ProductAmountPair;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductDto.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductDto.ProductOptionInfo;
import kr.hhplus.be.server.domain.product.ProductOption;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String productName;
  private String productDescription;
  private String productOptionName;
  private String productOptionDescription;
  private Long basePrice;
  private Long additionalPrice;
  private Long totalPrice;
  private Long amount;
  private Long productOptionId;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @Builder
  public OrderItem(Long id, String productName, String productDescription, String productOptionName,
      String productOptionDescription, Long basePrice, Long additionalPrice, Long totalPrice,
      Long amount, Order order, Long productOptionId) {
    if (basePrice != null && basePrice < 0) {
      throw new OrderItemIllegalStateException("상품 기본 가격은 0 이상이어야 합니다.");
    }

    if (additionalPrice != null && additionalPrice < 0) {
      throw new OrderItemIllegalStateException("상품 옵션 가격은 0 이상이어야 합니다.");
    }

    if (totalPrice != null && totalPrice < 0) {
      throw new OrderItemIllegalStateException("상품 총 가격은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.productName = productName;
    this.productDescription = productDescription;
    this.productOptionName = productOptionName;
    this.productOptionDescription = productOptionDescription;
    this.basePrice = basePrice;
    this.additionalPrice = additionalPrice;
    this.totalPrice = totalPrice;
    this.amount = amount;
    this.order = order;
    this.productOptionId = productOptionId;
  }

  public static OrderItem create(ProductAmountPair productAmountPair) {
    ProductInfo product = productAmountPair.product();
    ProductOptionInfo productOption = productAmountPair.productOption();

    long totalPrice = product.basePrice() + productOption.additionalPrice();
    return OrderItem.builder()
        .productName(product.name())
        .productDescription(product.description())
        .productOptionName(productOption.name())
        .productOptionDescription(productOption.description())
        .productOptionId(productOption.id())
        .basePrice(product.basePrice())
        .additionalPrice(productOption.additionalPrice())
        .totalPrice(totalPrice * productAmountPair.amount())
        .amount(productAmountPair.amount())
        .build();
  }

  public void setupOrder(Order order) {
    this.order = order;
    order.getOrderItems().add(this);
  }
}