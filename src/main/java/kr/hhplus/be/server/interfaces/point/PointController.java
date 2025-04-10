package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.point.UserPoint;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.point.PointResponse.ChargePointResponse;
import kr.hhplus.be.server.interfaces.point.PointResponse.CurrentPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController implements PointControllerSpec {

  private final PointService pointService;

  @PostMapping("/{userId}/charge")
  public CommonResponseWrapper<ChargePointResponse> chargePoint(@PathVariable long userId, @RequestBody long amount) {
    long remainingPoint = pointService.charge(userId, amount);

    ChargePointResponse response = new ChargePointResponse(userId, remainingPoint);

    return CommonResponseWrapper.ok(response);
  }

  @GetMapping("/{userId}")
  public CommonResponseWrapper<CurrentPointResponse> findPoint(@PathVariable long userId) {
    UserPoint userPoint = pointService.findUserPointByUserId(userId);

    CurrentPointResponse response = CurrentPointResponse.from(userPoint);

    return CommonResponseWrapper.ok(response);
  }
}
