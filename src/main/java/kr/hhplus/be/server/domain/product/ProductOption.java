package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import kr.hhplus.be.server.domain.BaseEntity;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @OneToOne(mappedBy = "productOption")
  private ProductInventory productInventory;

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

  public void setupProduct(Product product) {
    this.product = product;
    product.getProductOptions().add(this);
  }

  public void setupProductInventory(ProductInventory productInventory) {
    this.productInventory = productInventory;
  }
}