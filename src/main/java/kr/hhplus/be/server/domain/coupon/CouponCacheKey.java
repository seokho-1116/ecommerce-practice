package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.CacheKey;
import kr.hhplus.be.server.support.CacheKeyHolder;

public enum CouponCacheKey implements CacheKey {
  COUPON_EVENT_QUEUE("coupon_event_queue"),
  COUPON("coupon"),
  COUPON_EVENT("coupon_event");

  private final String key;

  CouponCacheKey(String key) {
    this.key = key;
  }

  @Override
  public <T> CacheKeyHolder<T> value(T value) {
    return new CacheKeyHolder<>(this, value);
  }

  @Override
  public <T> String generate(T value) {
    if (value == null) {
      return key;
    }

    return key + ":" + value;
  }
}
