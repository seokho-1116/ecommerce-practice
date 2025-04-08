package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductOptionIllegalStateException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductOption extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private Long additionalPrice;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Builder
  public ProductOption(Long id, String name, String description, Long additionalPrice,
      Product product) {
    if (additionalPrice != null && additionalPrice < 0) {
      throw new ProductOptionIllegalStateException("상품 옵션 가격은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.additionalPrice = additionalPrice;
    this.product = product;
  }

  public OrderItem createOrderItem() {
    long totalPrice = product.getBasePrice() + additionalPrice;
    return OrderItem.builder()
        .productName(product.getName())
        .productDescription(product.getDescription())
        .productOptionName(name)
        .productOptionDescription(description)
        .basePrice(product.getBasePrice())
        .additionalPrice(additionalPrice)
        .discountPrice(0L)
        .totalPrice(totalPrice)
        .build();
  }
}