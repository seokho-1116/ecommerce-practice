package kr.hhplus.be.server.infrastructure.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kr.hhplus.be.server.common.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public void save(String key, Object value) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, jsonValue);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public void save(String key, Object value, long expireTime, TimeUnit timeUnit) {
    try {
      String jsonValue = objectMapper.writeValueAsString(value);
      redisTemplate.opsForValue().set(key, jsonValue, expireTime, timeUnit);
    } catch (Exception e) {
      throw new ServerException(e);
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
      throw new ServerException(e);
    }
  }

  public void upsertScoreInZset(String key, String value, double score) {
    try {
      redisTemplate.opsForZSet().incrementScore(key, value, score);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public <T> List<Pair<T, Long>> findReverseRangeInZsetWithRank(String key, long start, long end,
      TypeReference<T> typeReference) {
    Set<TypedTuple<String>> result = redisTemplate.opsForZSet()
        .reverseRangeWithScores(key, start, end);
    if (result == null) {
      return List.of();
    }

    return result.stream()
        .map(tuple -> {
          try {
            T value = objectMapper.readValue(tuple.getValue(), typeReference);
            return Pair.of(value, tuple.getScore() == null ? 0L : tuple.getScore().longValue());
          } catch (Exception e) {
            throw new ServerException(e);
          }
        })
        .toList();
  }

  public <T> List<T> findRangeInZset(String key, long start, long end,
      TypeReference<T> typeReference) {
    Set<String> result = redisTemplate.opsForZSet().range(key, start, end);
    if (result == null || result.isEmpty()) {
      return List.of();
    }

    return result.stream()
        .map(value -> {
          try {
            return objectMapper.readValue(value, typeReference);
          } catch (Exception e) {
            throw new ServerException(e);
          }
        })
        .toList();
  }

  public <T> List<T> findReverseRangeInZset(String key, long start, long end,
      TypeReference<T> typeReference) {
    Set<String> result = redisTemplate.opsForZSet().reverseRange(key, start, end);
    if (result == null || result.isEmpty()) {
      return List.of();
    }

    return result.stream()
        .map(value -> {
          try {
            return objectMapper.readValue(value, typeReference);
          } catch (Exception e) {
            throw new ServerException(e);
          }
        })
        .toList();
  }

  public void saveIfAbsent(String key, String value, long currentTimeMillis) {
    try {
      redisTemplate.opsForZSet().addIfAbsent(key, value, currentTimeMillis);
    } catch (Exception e) {
      throw new ServerException(e);
    }
  }

  public void delete(String key) {
    redisTemplate.delete(key);
  }
}
