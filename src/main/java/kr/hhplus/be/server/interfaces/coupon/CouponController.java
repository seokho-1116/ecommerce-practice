package kr.hhplus.be.server.interfaces.coupon;

import jakarta.validation.Valid;
import java.util.List;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponEventCommand;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponDto.CouponIssueInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.coupon.CouponRequest.CouponEventRequest;
import kr.hhplus.be.server.interfaces.coupon.CouponRequest.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse.CouponIssueResponse;
import kr.hhplus.be.server.interfaces.coupon.CouponResponse.CouponSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController implements CouponControllerSpec {

  private final CouponService couponService;

  @GetMapping
  public CommonResponseWrapper<List<CouponSummaryResponse>> findAllCoupons() {
    List<Coupon> coupons = couponService.findAllCoupons();

    List<CouponSummaryResponse> responses = CouponSummaryResponse.from(coupons);

    return CommonResponseWrapper.ok(responses);
  }

  @PostMapping("/issue")
  public CommonResponseWrapper<CouponIssueResponse> issue(
      @RequestBody @Valid CouponIssueRequest request) {
    CouponIssueInfo userCoupon = couponService.issue(request.userId(), request.couponId());

    CouponIssueResponse response = CouponIssueResponse.from(userCoupon);

    return CommonResponseWrapper.ok(response);
  }

  @PostMapping("/event")
  public CommonResponseWrapper<CouponSummaryResponse> saveCouponEvent(
      @RequestBody @Valid CouponEventRequest request) {
    CouponEventCommand event = request.toCommand();

    CouponInfo couponInfo = couponService.saveCouponEvent(event);

    CouponSummaryResponse response = CouponSummaryResponse.from(couponInfo);
    return CommonResponseWrapper.ok(response);
  }
}
