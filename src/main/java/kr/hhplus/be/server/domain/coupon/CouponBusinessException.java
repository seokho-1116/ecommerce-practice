package kr.hhplus.be.server.domain.coupon;

public class CouponBusinessException extends RuntimeException {

  public CouponBusinessException(String message) {
    super(message);
  }

  public static class CouponIllegalStateException extends CouponBusinessException {
    public CouponIllegalStateException(String message) {
      super(message);
    }
  }
}
