package kr.hhplus.be.server.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TestHelpRepository {

  private final EntityManager entityManager;

  public TestHelpRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
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
}
