package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.controller.response.ChargePointResponse;
import kr.hhplus.be.server.controller.response.CommonResponseWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/point")
public class PointController {

  @PostMapping("/{id}/charge")
  public CommonResponseWrapper<ChargePointResponse> chargePoint(@PathVariable long id) {
    return CommonResponseWrapper.ok(
        new ChargePointResponse(id, 1000L)
    );
  }

  @GetMapping("/{id}")
  public CommonResponseWrapper<CurrentPointResponse> findPoint(@PathVariable long id) {
    return CommonResponseWrapper.ok(
        new CurrentPointResponse(id, 1000L)
    );
  }
}
