package kr.hhplus.be.server.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import kr.hhplus.be.server.common.exception.ServerException;
import kr.hhplus.be.server.support.CacheKey;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestHelpRepository {

  private final EntityManager entityManager;
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public TestHelpRepository(EntityManager entityManager, StringRedisTemplate stringRedisTemplate,
      ObjectMapper objectMapper) {
    this.entityManager = entityManager;
    this.redisTemplate = stringRedisTemplate;
    this.objectMapper = objectMapper;
  }

  public <T> T save(T entity) {
    Session session = entityManager.unwrap(Session.class);
    StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();

    Transaction transaction = null;
    try (statelessSession) {
      transaction = statelessSession.beginTransaction();
      statelessSession.insert(entity);

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw e;
    }
    return entity;
  }

  public void cleanup() {
    List<String> entityNames = entityManager.getMetamodel().getEntities().stream()
        .map(this::getTableName)
        .toList();

    Session session = entityManager.unwrap(Session.class);
    StatelessSession statelessSession = session.getSessionFactory().openStatelessSession();
    Transaction transaction = null;

    try (statelessSession) {
      transaction = statelessSession.beginTransaction();
      for (String entityName : entityNames) {
        statelessSession.createNativeQuery("TRUNCATE TABLE `" + entityName + "`").executeUpdate();
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null && transaction.isActive()) {
        transaction.rollback();
      }
      throw e;
    }
  }


  private String getTableName(EntityType<?> entityType) {
    Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);

    return Optional.ofNullable(tableAnnotation)
        .filter(this::isNotEmptyTableName)
        .map(Table::name)
        .orElse(toSnakeCaseEntityName(entityType));
  }

  private boolean isNotEmptyTableName(Table table) {
    return !table.name().isEmpty();
  }

  private String toSnakeCaseEntityName(EntityType<?> entityType) {
    return entityType.getName().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
  }

  public <T> T findInCache(CacheKey key, TypeReference<T> typeReference) {
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

  public void cleanCache() {
    redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
  }

  public <T> Set<T> findZsetInCache(String key, long start, long end,
      TypeReference<T> typeReference) {
    Set<String> result = redisTemplate.opsForZSet().range(key, start, end);

    if (result == null || result.isEmpty()) {
      return Set.of();
    }

    return result.stream()
        .map(value -> {
          try {
            return objectMapper.readValue(value, typeReference);
          } catch (Exception e) {
            throw new ServerException(e);
          }
        })
        .collect(Collectors.toSet());
  }
}
