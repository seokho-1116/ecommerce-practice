package kr.hhplus.be.server.infrastructure.point;

import java.util.Optional;
import kr.hhplus.be.server.domain.point.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointJpaRepository extends JpaRepository<UserPoint, Long> {

  Optional<UserPoint> findByUserId(Long userId);
}
