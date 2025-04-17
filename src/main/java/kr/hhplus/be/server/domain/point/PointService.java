package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.PointBusinessException.UserPointNotFoundException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final PointRepository pointRepository;
  private final UserRepository userRepository;

  public long use(Long userId, Long amount) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    UserPoint userPoint = pointRepository.findByUserId(userId)
        .orElseGet(() -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new UserPointNotFoundException("유저를 찾을 수 없습니다."));

          UserPoint newUserPoint = UserPoint.builder()
              .user(user)
              .amount(0L)
              .build();

          pointRepository.save(newUserPoint);
          return newUserPoint;
        });

    userPoint.use(amount);

    PointHistory pointHistory = PointHistory.useHistory(userPoint.getUser(), amount);
    pointRepository.savePointHistory(pointHistory);

    pointRepository.save(userPoint);

    return userPoint.getAmount();
  }

  public long charge(Long userId, Long amount) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    UserPoint userPoint = pointRepository.findByUserId(userId)
        .orElseGet(() -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new UserPointNotFoundException("유저를 찾을 수 없습니다."));

          UserPoint newUserPoint = UserPoint.builder()
              .user(user)
              .amount(0L)
              .build();

          pointRepository.save(newUserPoint);
          return newUserPoint;
        });

    userPoint.charge(amount);

    PointHistory pointHistory = PointHistory.chargeHistory(userPoint.getUser(), amount);
    pointRepository.savePointHistory(pointHistory);

    pointRepository.save(userPoint);

    return userPoint.getAmount();
  }

  public UserPoint findUserPointByUserId(Long userId) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    return pointRepository.findByUserId(userId)
        .orElseThrow(() -> new UserPointNotFoundException("유저 포인트를 찾을 수 없습니다."));
  }
}
