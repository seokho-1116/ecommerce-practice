package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.point.PointBusinessException.UserPointIllegalStateException;
import kr.hhplus.be.server.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserPoint extends BaseEntity {

  private static final long MAX_POINT = 100_000_000L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long amount;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public UserPoint(Long id, Long amount, User user) {
    if (amount != null && amount < 0) {
      throw new UserPointIllegalStateException("포인트는 0 이상이어야 합니다.");
    }

    if (amount != null && amount > MAX_POINT) {
      throw new UserPointIllegalStateException("포인트는 최대 " + MAX_POINT + "까지 사용 가능합니다.");
    }

    this.id = id;
    this.amount = amount;
    this.user = user;
  }

  public long use(Long amount) {
    if (amount == null) {
      throw new UserPointIllegalStateException("사용할 포인트는 null일 수 없습니다.");
    }

    if (amount < 0) {
      throw new UserPointIllegalStateException("사용할 포인트는 0 이상이어야 합니다.");
    }

    if (this.amount < amount) {
      throw new UserPointIllegalStateException("사용할 포인트가 부족합니다.");
    }

    this.amount -= amount;

    return this.amount;
  }

  public static long getMaxPoint() {
    return MAX_POINT;
  }
}