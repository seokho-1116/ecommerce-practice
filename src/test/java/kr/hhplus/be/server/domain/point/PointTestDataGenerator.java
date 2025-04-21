package kr.hhplus.be.server.domain.point;

import static org.instancio.Select.field;

import org.instancio.Instancio;
import org.springframework.stereotype.Component;

@Component
public class PointTestDataGenerator {

  public UserPoint userPoint(Long userId) {
    return Instancio.of(UserPoint.class)
        .ignore(field(UserPoint::getId))
        .set(field(UserPoint::getIsActive), true)
        .set(field(UserPoint::getUserId), userId)
        .ignore(field(UserPoint::getCreatedAt))
        .ignore(field(UserPoint::getUpdatedAt))
        .create();
  }
}
