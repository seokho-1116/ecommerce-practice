package kr.hhplus.be.server.support;

public record CacheKeyHolder<T>(
    CacheKey key,
    T value
) {

  public String generate() {
    return key.generate(value);
  }
}