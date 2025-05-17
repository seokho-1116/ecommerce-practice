package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.support.CacheKey;
import kr.hhplus.be.server.support.CacheKeyHolder;

public enum ProductCacheKey implements CacheKey {
  TOP5_SELLING_PRODUCT("top5_selling_product"),
  PRODUCT_SELLING_RANK("product_selling_rank");

  private final String key;

  ProductCacheKey(String key) {
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
