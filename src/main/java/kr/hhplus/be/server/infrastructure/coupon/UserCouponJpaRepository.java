package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

public interface UserCouponJpaRepository extends JpaRepository<UserCoupon, Long> {

  List<UserCoupon> findAllByUserId(Long userId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
  Optional<UserCoupon> findForUpdateByUserIdAndCouponId(Long userId, Long couponId);

  List<UserCoupon> findAllByUserIdAndOrderId(Long userId, Long orderId);

  List<UserCoupon> findAll(Specification<UserCoupon> spec);
}
