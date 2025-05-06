package kr.hhplus.be.server.support;

public enum CacheKey {
  COUPON_ISSUE("coupon:issue"),;

  private final String key;

  CacheKey(String key) {
    this.key = key;
  }

  public String appendAfterColon(String key) {
    return this.name() + ":" + key;
  }
}
