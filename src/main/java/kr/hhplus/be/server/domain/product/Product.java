package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.product.ProductBusinessException.ProductIllegalStateException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private Long basePrice;

  @OneToMany(mappedBy = "product")
  private List<ProductOption> productOptions;

  @Builder
  public Product(Long id, String name, String description, Long basePrice) {
    if (basePrice != null && basePrice < 0) {
      throw new ProductIllegalStateException("상품 가격은 0 이상이어야 합니다.");
    }

    this.id = id;
    this.name = name;
    this.description = description;
    this.basePrice = basePrice;
  }
}