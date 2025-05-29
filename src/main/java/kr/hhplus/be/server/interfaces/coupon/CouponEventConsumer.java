package kr.hhplus.be.server.interfaces.coupon;

import java.util.List;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponBatchIssueCommand;
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

  @KafkaListener(topics = "coupon.v1.issue", batch = "true", concurrency = "1")
  public void handleCouponBatchIssueEvent(List<CouponIssueEvent> events,
      @Header(KafkaHeaders.RECEIVED_KEY) List<String> couponIds,
      Acknowledgment ack) {
    String couponId = couponIds.get(0);
    CouponBatchIssueCommand command = CouponBatchIssueCommand.of(Long.valueOf(couponId), events);
    couponService.issueAllFromQueue(command);
    ack.acknowledge();
  }
}