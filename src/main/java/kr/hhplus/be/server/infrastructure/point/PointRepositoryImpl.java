package kr.hhplus.be.server.infrastructure.point;

import java.util.Optional;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

  private final PointJpaRepository pointJpaRepository;
  private final PointHistoryJpaRepository pointHistoryJpaRepository;

  @Override
  public Optional<UserPoint> findByUserId(Long userId) {
    return pointJpaRepository.findByUserId(userId);
  }

  @Override
  public void save(UserPoint userPoint) {
    pointJpaRepository.save(userPoint);
  }

  @Override
  public void savePointHistory(PointHistory pointHistory) {
    pointHistoryJpaRepository.save(pointHistory);
  }
}
