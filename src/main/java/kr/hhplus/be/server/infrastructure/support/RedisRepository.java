package kr.hhplus.be.server.infrastructure.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.common.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public void save(String key, Object value, long expireTime, TimeUnit timeUnit) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, jsonValue, expireTime, timeUnit);
    } catch (Exception e) {
      throw new ServerException();
    }
  }

  public <T> T find(String key, TypeReference<T> typeReference) {
    try {
      String jsonValue = redisTemplate.opsForValue().get(key);
      if (jsonValue == null) {
        return null;
      }

      return objectMapper.readValue(jsonValue, typeReference);
    } catch (Exception e) {
      throw new ServerException();
    }
  }
}
