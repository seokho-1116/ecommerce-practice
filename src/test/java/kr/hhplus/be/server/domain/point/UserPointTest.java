package kr.hhplus.be.server.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.hhplus.be.server.domain.point.PointBusinessException.UserPointIllegalStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserPointTest {

  @DisplayName("포인트가 0보다 크면 포인트가 차감된다")
  @Test
  void createUserPointTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(1000L)
        .build();
    long useAmount = 100L;

    //when
    long remain = userPoint.use(useAmount);

    //then
    assertThat(remain).isEqualTo(900L);
  }

  @DisplayName("사용 후 포인트가 0보다 작으면 포인트 상태 예외가 발생한다")
  @Test
  void createUserPointWithNegativeAmountAfterUseTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(1000L)
        .build();
    long useAmount = 2000L;

    //when
    //then
    assertThatThrownBy(() -> userPoint.use(useAmount))
        .isInstanceOf(UserPointIllegalStateException.class);
  }

  @DisplayName("사용할 포인트가 0보다 작으면 포인트 상태 예외가 발생한다")
  @Test
  void createUserPointWithNegativeUseAmountTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(1000L)
        .build();
    long useAmount = -1000L;

    //when
    //then
    assertThatThrownBy(() -> userPoint.use(useAmount))
        .isInstanceOf(UserPointIllegalStateException.class);
  }

  @DisplayName("포인트가 0보다 크면 포인트가 충전된다")
  @Test
  void chargeUserPointTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(1000L)
        .build();
    long chargeAmount = 100L;

    //when
    long remain = userPoint.charge(chargeAmount);

    //then
    assertThat(remain).isEqualTo(1100L);
  }

  @DisplayName("충전 후 포인트가 최대값보다 크면 포인트 상태 예외가 발생한다")
  @Test
  void chargeUserPointWithOverMaxAmountTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(UserPoint.getMaxPoint())
        .build();
    long chargeAmount = 2000L;

    //when
    //then
    assertThatThrownBy(() -> userPoint.charge(chargeAmount))
        .isInstanceOf(UserPointIllegalStateException.class);
  }

  @DisplayName("충전할 포인트가 0보다 작으면 포인트 상태 예외가 발생한다")
  @Test
  void chargeUserPointWithNegativeChargeAmountTest() {
    //given
    UserPoint userPoint = UserPoint.builder()
        .id(1L)
        .amount(1000L)
        .build();
    long chargeAmount = -1000L;

    //when
    //then
    assertThatThrownBy(() -> userPoint.charge(chargeAmount))
        .isInstanceOf(UserPointIllegalStateException.class);
  }

  @DisplayName("포인트는 0보다 작을 수 없다")
  @Test
  void constructorWithNegativeAmountTest() {
    //given
    UserPoint.UserPointBuilder userPointBuilder = UserPoint.builder()
        .id(1L)
        .amount(-1000L);

    //when
    //then
    assertThatThrownBy(userPointBuilder::build)
        .isInstanceOf(UserPointIllegalStateException.class);
  }

  @DisplayName("포인트는 0일 수 있다")
  @Test
  void constructorWithZeroAmountTest() {
    //given
    UserPoint.UserPointBuilder userPointBuilder = UserPoint.builder()
        .id(1L)
        .amount(0L);

    //when
    UserPoint userPoint = userPointBuilder.build();

    //then
    assertThat(userPoint.getAmount()).isZero();
  }

  @DisplayName("포인트는 최대값보다 클 수 없다")
  @Test
  void constructorWithOverMaxAmountTest() {
    //given
    UserPoint.UserPointBuilder userPointBuilder = UserPoint.builder()
        .id(1L)
        .amount(UserPoint.getMaxPoint() + 1);

    //when
    //then
    assertThatThrownBy(userPointBuilder::build)
        .isInstanceOf(UserPointIllegalStateException.class);
  }
}