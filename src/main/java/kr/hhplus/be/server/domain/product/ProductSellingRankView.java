package kr.hhplus.be.server.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductSellingRankView extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;
  private Long sumOfSellingAmount;
  private Long rank;
  private LocalDateTime from;
  private LocalDateTime to;

  @Builder
  public ProductSellingRankView(Long id, Long productId, Long sumOfSellingAmount, Long rank,
      LocalDateTime from, LocalDateTime to) {
    this.id = id;
    this.productId = productId;
    this.sumOfSellingAmount = sumOfSellingAmount;
    this.rank = rank;
    this.from = from;
    this.to = to;
  }
}
