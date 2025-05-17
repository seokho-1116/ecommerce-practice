package kr.hhplus.be.server.support;

public interface CacheKey {

  <T> CacheKeyHolder<T> value(T value);

  <T> String generate(T value);
}
