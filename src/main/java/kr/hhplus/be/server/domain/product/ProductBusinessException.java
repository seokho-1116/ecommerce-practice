package kr.hhplus.be.server.domain.product;

public class ProductBusinessException extends RuntimeException {

  public ProductBusinessException(String message) {
    super(message);
  }

  public static class ProductIllegalStateException extends ProductBusinessException {
    public ProductIllegalStateException(String message) {
      super(message);
    }
  }

  public static class ProductOptionIllegalStateException extends ProductBusinessException {
    public ProductOptionIllegalStateException(String message) {
      super(message);
    }
  }

  public static class ProductInventoryIllegalStateException extends ProductBusinessException {
    public ProductInventoryIllegalStateException(String message) {
      super(message);
    }
  }
}
