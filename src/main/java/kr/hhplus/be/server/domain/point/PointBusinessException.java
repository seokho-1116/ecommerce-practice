package kr.hhplus.be.server.domain.point;

public class PointBusinessException extends RuntimeException {

  public PointBusinessException(String message) {
    super(message);
  }

  public static class UserPointIllegalStateException extends PointBusinessException {

    public UserPointIllegalStateException(String message) {
      super(message);
    }
  }
}
