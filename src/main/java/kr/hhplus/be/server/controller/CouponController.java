package kr.hhplus.be.server.controller;

import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.response.CouponIssueResponse;
import kr.hhplus.be.server.controller.response.CouponSummaryResponse;
import kr.hhplus.be.server.controller.spec.CouponControllerSpec;
import kr.hhplus.be.server.domain.coupon.CouponType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerSpec {

  @GetMapping
  public CommonResponseWrapper<List<CouponSummaryResponse>> findAllCoupons() {
    return CommonResponseWrapper.ok(
        List.of(
            new CouponSummaryResponse(
                1L,
                1L,
                "쿠폰 이름",
                0.1,
                null,
                CouponType.PERCENTAGE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now()
            )
        )
    );
  }

  @PostMapping("/issue")
  public CommonResponseWrapper<CouponIssueResponse> issue() {
    return CommonResponseWrapper.ok(
        new CouponIssueResponse(
            1L,
            1L,
            1L,
            "쿠폰 이름",
            null,
            1000L,
            CouponType.FIXED,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now()
        )
    );
  }
}
