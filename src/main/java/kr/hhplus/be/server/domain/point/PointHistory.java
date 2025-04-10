package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PointHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long amount;
  private TransactionType transactionType;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public PointHistory(Long id, Long amount, TransactionType transactionType, User user) {
    this.id = id;
    this.amount = amount;
    this.transactionType = transactionType;
    this.user = user;
  }

  public static PointHistory useHistory(User user, long amount) {
    return PointHistory.builder()
        .amount(amount)
        .user(user)
        .transactionType(TransactionType.USE)
        .build();
  }

  public static PointHistory chargeHistory(User user, Long amount) {
    return null;
  }
}
