package kr.hhplus.be.server.interfaces.coupon;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponEvent.CouponIssueEvent;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventConsumer {

  private final CouponService couponService;

  @KafkaListener(topics = "coupon.v1.issue", groupId = "coupon-group", batch = "true")
  public void handleCouponBatchIssueEvent(List<CouponIssueEvent> events,
      @Header(KafkaHeaders.KEY) String couponId,
      Acknowledgment ack) {
    List<CouponIssueCommand> commands = events.stream()
        .map(CouponIssueCommand::from)
        .toList();
    couponService.issueAllFromQueue(Long.valueOf(couponId), commands);
    ack.acknowledge();
  }
}
