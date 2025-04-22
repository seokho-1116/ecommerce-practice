package kr.hhplus.be.server.domain.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CountDownLatch;
import kr.hhplus.be.server.IntegrationTestSupport;
import kr.hhplus.be.server.common.TestReflectionUtil;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserTestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PointServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private PointService pointService;

  @Autowired
  private UserTestDataGenerator userTestDataGenerator;

  @Autowired
  private PointTestDataGenerator pointTestDataGenerator;

  private User user;
  private UserPoint userPoint;

  private User noPointUser;

  @BeforeEach
  void setup() {
    user = userTestDataGenerator.user();
    testHelpRepository.save(user);

    userPoint = pointTestDataGenerator.userPoint(user.getId());
    TestReflectionUtil.setField(userPoint, "amount", 1000000L);
    testHelpRepository.save(userPoint);

    noPointUser = userTestDataGenerator.user();
    testHelpRepository.save(noPointUser);
  }

  @DisplayName("기존 포인트가 없을 때 포인트를 충전하면 현재 포인트에서 충전한 포인트를 더한 값이 반환된다")
  @Test
  void chargeTest() {
    // given
    long noPointUserId = noPointUser.getId();
    long amount = 1000L;

    // when
    long remainingPoint = pointService.charge(noPointUserId, amount);

    // then
    assertThat(remainingPoint).isEqualTo(amount);
  }

  @DisplayName("기존 포인트가 있을 때 포인트를 충전하면 현재 포인트에서 충전한 포인트를 더한 값이 반환된다")
  @Test
  void chargeWithExistingPointTest() {
    // given
    long userId = user.getId();
    long initialAmount = userPoint.getAmount();
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
    long userId = user.getId();
    long initialAmount = userPoint.getAmount();
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
    long noPointUserId = noPointUser.getId();
    long useAmount = 500L;

    // when & then
    assertThatThrownBy(() -> pointService.use(noPointUserId, useAmount))
        .isInstanceOf(PointBusinessException.class);
  }

  @DisplayName("동시에 포인트를 충전하면 포인트가 수량만큼만 충전된다")
  @Test
  void concurrentChargeTest() throws InterruptedException {
    // given
    int concurrentRequest = 10;
    long amount = 1000L;
    long userId = user.getId();
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          pointService.charge(userId, amount);
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    UserPoint result = pointService.findUserPointByUserId(userId);
    assertThat(result.getAmount()).isEqualTo(userPoint.getAmount() + (amount * concurrentRequest));
  }

  @DisplayName("동시에 포인트를 사용하면 포인트가 수량만큼만 사용된다")
  @Test
  void concurrentUseTest() throws InterruptedException {
    // given
    int concurrentRequest = 10;
    long amount = 1000L;
    long userId = user.getId();
    CountDownLatch latch = new CountDownLatch(concurrentRequest);

    // when
    for (int i = 0; i < concurrentRequest; i++) {
      new Thread(() -> {
        try {
          pointService.use(userId, amount);
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();

    // then
    UserPoint result = pointService.findUserPointByUserId(userId);
    assertThat(result.getAmount()).isEqualTo(userPoint.getAmount() - (amount * concurrentRequest));
  }
}
