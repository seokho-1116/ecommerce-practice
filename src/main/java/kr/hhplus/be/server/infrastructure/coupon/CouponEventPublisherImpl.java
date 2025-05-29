package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEvent.CouponIssueEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventPublisherImpl implements CouponEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Override
  public void issueCoupon(CouponIssueEvent event) {
    kafkaTemplate.send("coupon.v1.issue", String.valueOf(event.couponId()), event);
  }
}
