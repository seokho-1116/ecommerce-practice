package kr.hhplus.be.server.interfaces.payment;


import jakarta.validation.Valid;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.interfaces.CommonResponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController implements PaymentControllerSpec {

  private final PaymentFacade paymentFacade;

  @PostMapping("/{orderId}")
  public CommonResponseWrapper<OrderPaymentResponse> paymentOrder(
      @PathVariable long orderId,
      @RequestBody @Valid OrderPaymentRequest request
  ) {
    PaymentCommand command = request.toCommand(orderId);
    PaymentResult paymentResult = paymentFacade.payment(command);

    OrderPaymentResponse response = OrderPaymentResponse.from(paymentResult);
    return CommonResponseWrapper.ok(response);
  }
}
