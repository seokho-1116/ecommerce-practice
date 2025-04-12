package kr.hhplus.be.server.domain.coupon;

public enum CouponType {
  FIXED(
      (totalPrice, discountRate, discountAmount) -> Math.min(discountAmount, totalPrice)
  ),
  PERCENTAGE(
      (totalPrice, discountRate, discountAmount) -> Math.min((long) (totalPrice * discountRate),
          totalPrice)
  );

  private final DiscountStrategy discountStrategy;

  CouponType(DiscountStrategy discountStrategy) {
    this.discountStrategy = discountStrategy;
  }

  public long calculateDiscountPrice(long totalPrice, Double discountRate, Long discountAmount) {
    if (totalPrice < 0) {
      return 0;
    }
    return discountStrategy.apply(totalPrice, discountRate, discountAmount);
  }

  @FunctionalInterface
  private interface DiscountStrategy {

    long apply(long totalPrice, Double discountRate, Long discountAmount);
  }
}
