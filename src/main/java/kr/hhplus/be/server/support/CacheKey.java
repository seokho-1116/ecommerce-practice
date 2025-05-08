package kr.hhplus.be.server.support;

public enum CacheKey {
  COUPON_ISSUE("coupon:issue"),;

  private final String key;

  CacheKey(String key) {
    this.key = key;
  }

  public String appendAfterColon(String input) {
    if (input == null || input.isEmpty()) {
      return this.key;
    }

    return this.key + ":" + input;
  }
}
