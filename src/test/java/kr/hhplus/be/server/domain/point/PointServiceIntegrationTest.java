package kr.hhplus.be.server.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
@Sql(scripts = "classpath:db/point_test_case.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:db/cleanup.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class PointServiceIntegrationTest {

  @Autowired
  private PointService pointService;

  @DisplayName("기존 포인트가 없을 때 포인트를 충전하면 현재 포인트에서 충전한 포인트를 더한 값이 반환된다")
  @Test
  void chargeTest() {
    // given
    long userId = 2L;
    long amount = 1000L;

    // when
    long remainingPoint = pointService.charge(userId, amount);

    // then
    assertThat(remainingPoint).isEqualTo(amount);
  }

  @DisplayName("기존 포인트가 있을 때 포인트를 충전하면 현재 포인트에서 충전한 포인트를 더한 값이 반환된다")
  @Test
  void chargeWithExistingPointTest() {
    // given
    long userId = 1L;
    long initialAmount = 1000L;
    long chargeAmount = 500L;

    // when
    long remainingPoint = pointService.charge(userId, chargeAmount);

    // then
    assertThat(remainingPoint).isEqualTo(initialAmount + chargeAmount);
  }

  @DisplayName("기존 포인트가 있을 때 포인트를 사용하면 현재 포인트에서 사용한 포인트를 뺀 값이 반환된다")
  @Test
  void useTest() {
    // given
    long userId = 1L;
    long initialAmount = 1000L;
    long useAmount = 500L;

    // when
    long remainingPoint = pointService.use(userId, useAmount);

    // then
    assertThat(remainingPoint).isEqualTo(initialAmount - useAmount);
  }

  @DisplayName("기존 포인트가 없을 때 포인트를 사용하면 예외가 발생한다")
  @Test
  void useWithNoExistingPointTest() {
    // given
    long userId = 2L;
    long useAmount = 500L;

    // when & then
    assertThatThrownBy(() -> pointService.use(userId, useAmount))
        .isInstanceOf(PointBusinessException.class);
  }
}
