package kr.hhplus.be.server.domain.order;

public class OrderBusinessException extends RuntimeException {

  public OrderBusinessException(String message) {
    super(message);
  }

  public static class OrderCommandIllegalStateException extends OrderBusinessException {
    public OrderCommandIllegalStateException(String message) {
      super(message);
    }
  }

  public static class OrderIllegalStateException extends OrderBusinessException {
    public OrderIllegalStateException(String message) {
      super(message);
    }
  }

  public static class OrderItemIllegalStateException extends OrderBusinessException {
    public OrderItemIllegalStateException(String message) {
      super(message);
    }
  }

  public static class OrderNotFoundException extends OrderBusinessException {
    public OrderNotFoundException(String message) {
      super(message);
    }
  }
}
