package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.point.PointBusinessException.UserPointNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PointService {

  private PointRepository pointRepository;

  public long use(Long userId, Long amount) {
    if (userId == null) {
      throw new PointBusinessException("유저 ID는 null일 수 없습니다.");
    }

    UserPoint userPoint = pointRepository.findByUserId(userId)
        .orElseThrow(() -> new UserPointNotFoundException("유저 포인트를 찾을 수 없습니다."));

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
        .orElseThrow(() -> new UserPointNotFoundException("유저 포인트를 찾을 수 없습니다."));

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
