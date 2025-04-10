package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

  Optional<UserPoint> findByUserId(Long userId);

  void save(UserPoint userPoint);

  void savePointHistory(PointHistory pointHistory);
}
