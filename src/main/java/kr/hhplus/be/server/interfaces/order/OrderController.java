package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.order.OrderCommand.OrderCreateCommand;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import kr.hhplus.be.server.interfaces.order.OrderResponse.OrderSuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerSpec {

  private final OrderFacade orderFacade;

  @PostMapping
  public CommonResponseWrapper<OrderSuccessResponse> createOrder(@RequestBody OrderRequest request) {
    OrderCreateCommand command = request.toCreateCommand();
    OrderResult orderResult = orderFacade.createOrder(command);

    OrderSuccessResponse response = OrderSuccessResponse.from(orderResult);

    return CommonResponseWrapper.ok(response);
  }
}
