package kr.hhplus.be.server.support;

public enum LockKey {
  COUPON_ISSUE("coupon_issue");

  private final String key;

  LockKey(String key) {
    this.key = key;
  }

  public String generate(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("분산락의 키는 null 또는 공백이 될 수 없습니다.");
    }

    return key;
  }
}
