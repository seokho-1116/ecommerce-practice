package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.controller.response.ChargePointResponse;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import kr.hhplus.be.server.controller.spec.PointControllerSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/point")
public class PointController implements PointControllerSpec {

  @PostMapping("/{id}/charge")
  public CommonResponseWrapper<ChargePointResponse> chargePoint(@PathVariable long id, @RequestBody long amount) {
    return CommonResponseWrapper.ok(
        new ChargePointResponse(id, amount)
    );
  }

  @GetMapping("/{id}")
  public CommonResponseWrapper<CurrentPointResponse> findPoint(@PathVariable long id) {
    return CommonResponseWrapper.ok(
        new CurrentPointResponse(id, 1000L)
    );
  }
}
