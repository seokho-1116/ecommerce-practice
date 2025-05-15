package kr.hhplus.be.server.support;

import lombok.Getter;

@Getter
public enum CacheKey {
  COUPON_ISSUE("coupon:issue"),
  TOP5_SELLING_PRODUCT("top5_selling_product"),
  PRODUCT_SELLING_RANK("product_selling_rank"),
  COUPON_EVENT_QUEUE("coupon_event_queue"),
  COUPON("coupon");

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
