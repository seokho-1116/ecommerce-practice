package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductInventoryIllegalStateException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductInventory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long quantity;

  @OneToOne
  @JoinColumn(name = "product_option_id")
  private ProductOption productOption;

  @Builder
  public ProductInventory(Long id, Long quantity, ProductOption productOption) {
    if (quantity != null && quantity < 0) {
      throw new ProductInventoryIllegalStateException("재고 수량은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.quantity = quantity;
    this.productOption = productOption;
  }

  public void deduct(Long quantity) {
    if (this.quantity < quantity) {
      throw new ProductInventoryIllegalStateException("재고가 부족합니다.");
    }

    this.quantity -= quantity;
  }
}