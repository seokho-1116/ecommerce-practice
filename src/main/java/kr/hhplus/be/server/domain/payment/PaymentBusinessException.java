package kr.hhplus.be.server.domain.payment;

public class PaymentBusinessException extends RuntimeException {

  public PaymentBusinessException(String message) {
    super(message);
  }

  public static class PaymentIllegalStateException extends PaymentBusinessException {
    public PaymentIllegalStateException(String message) {
      super(message);
    }
  }

  public static class PaymentEventIllegalStateException extends PaymentBusinessException {
    public PaymentEventIllegalStateException(String message) {
      super(message);
    }
  }
}
