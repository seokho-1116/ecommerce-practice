package kr.hhplus.be.server.domain.point;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

  @Mock
  private PointRepository pointRepository;

  @InjectMocks
  private PointService pointService;

  @DisplayName("포인트 사용 시 포인트 내역이 저장되어야 한다")
  @Test
  void usePointTest() {
    // given
    Long userId = 1L;
    Long amount = 100L;
    UserPoint userPoint = UserPoint.builder()
        .amount(1000L)
        .build();

    when(pointRepository.findByUserId(userId))
        .thenReturn(Optional.of(userPoint));

    // when
    pointService.use(userId, amount);

    // then
    verify(pointRepository, atLeastOnce()).savePointHistory(any(PointHistory.class));
  }

  @DisplayName("포인트 사용 시 유저 아이디가 null인 경우 예외가 발생해야 한다")
  @Test
  void usePoint_NullUserId_ThrowsException() {
    // given
    Long userId = null;
    Long amount = 100L;

    // when & then
    assertThatThrownBy(() -> pointService.use(userId, amount))
        .isInstanceOf(PointBusinessException.class);
  }

  @DisplayName("포인트 충전 시 포인트 내역이 저장되어야 한다")
  @Test
  void chargePointTest() {
    // given
    Long userId = 1L;
    Long amount = 100L;
    UserPoint userPoint = UserPoint.builder()
        .amount(1000L)
        .build();

    when(pointRepository.findByUserId(userId))
        .thenReturn(Optional.of(userPoint));

    // when
    pointService.charge(userId, amount);

    // then
    verify(pointRepository, atLeastOnce()).savePointHistory(any(PointHistory.class));
  }

  @DisplayName("포인트 충전 시 유저 아이디가 null인 경우 예외가 발생해야 한다")
  @Test
  void chargePoint_NullUserId_ThrowsException() {
    // given
    Long userId = null;
    Long amount = 100L;

    // when & then
    assertThatThrownBy(() -> pointService.charge(userId, amount))
        .isInstanceOf(PointBusinessException.class);
  }

  @DisplayName("포인트 조회 시 유저 아이디가 null인 경우 예외가 발생해야 한다")
  @Test
  void findUserPointByUserId_NullUserId_ThrowsException() {
    // given
    Long userId = null;

    // when & then
    assertThatThrownBy(() -> pointService.findUserPointByUserId(userId))
        .isInstanceOf(PointBusinessException.class);
  }
}