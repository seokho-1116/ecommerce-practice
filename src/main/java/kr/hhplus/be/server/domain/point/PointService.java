package kr.hhplus.be.server.domain.point;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.point.PointBusinessException.UserPointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final PointRepository pointRepository;

  @Transactional
  public long use(Long userId, Long amount) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    UserPoint userPoint = pointRepository.findByUserId(userId)
        .orElseGet(() -> UserPoint.builder()
            .userId(userId)
            .amount(0L)
            .build());

    userPoint.use(amount);

    PointHistory pointHistory = PointHistory.useHistory(userPoint.getUserId(), amount);
    pointRepository.savePointHistory(pointHistory);

    pointRepository.save(userPoint);

    return userPoint.getAmount();
  }

  @Transactional
  public long charge(Long userId, Long amount) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    UserPoint userPoint = pointRepository.findByUserId(userId)
        .orElseGet(() -> UserPoint.builder()
            .userId(userId)
            .amount(0L)
            .build());

    userPoint.charge(amount);

    PointHistory pointHistory = PointHistory.chargeHistory(userPoint.getUserId(), amount);
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
