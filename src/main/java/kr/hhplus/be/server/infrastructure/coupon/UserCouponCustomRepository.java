package kr.hhplus.be.server.infrastructure.coupon;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCouponCustomRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public void saveAll(List<UserCoupon> userCoupons) {
    String sql = """
        INSERT INTO user_coupon (user_id, coupon_id, is_used, version, is_active, created_at, updated_at)
        VALUES (:userId, :coupon.id, :isUsed, 0, true, now(), now())
        """;

    namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(userCoupons));
  }
}
