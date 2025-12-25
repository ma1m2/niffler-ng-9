package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.model.CurrencyValues;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {

  private static final Config CFG = Config.getInstance();
  private final EntityManager entityManager = em(CFG.spendJdbcUrl());

  @Override
  public SpendEntity create(SpendEntity spend) {
    entityManager.joinTransaction();
    entityManager.persist(spend);
    return spend;
  }

  @Override
  public SpendEntity update(SpendEntity spend) {
    entityManager.joinTransaction();
    return entityManager.merge(spend);
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    entityManager.joinTransaction();
    entityManager.persist(category);
    return category;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return Optional.ofNullable(
            entityManager.find(CategoryEntity.class, id)
    );
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Optional<SpendEntity> findById(UUID id) {
    return Optional.ofNullable(
            entityManager.find(SpendEntity.class, id)
    );
  }

  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    try {
      return Optional.of(
              entityManager.createQuery(
                              "select s from SpendEntity s where s.username =: username and s.description =: description order by s.spendDate desc",
                              SpendEntity.class
                      ).setParameter("username", username)
                      .setParameter("description", description)
                      .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(SpendEntity spend) {
    entityManager.joinTransaction();
    entityManager.remove(entityManager.contains(spend) ? spend : entityManager.merge(spend));
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    entityManager.joinTransaction();
    entityManager.remove(entityManager.contains(category) ? category : entityManager.merge(category));
  }

  public List<SpendEntity> all(String username, @Nullable CurrencyValues currency, @Nullable Date from, @Nullable Date to) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SpendEntity> cq = cb.createQuery(SpendEntity.class);
    final Root<SpendEntity> root = cq.from(SpendEntity.class);

    final List<Predicate> predicates = new ArrayList<>();
    predicates.add(cb.equal(root.get("username"), username));
    if (currency != null) {
      predicates.add(cb.equal(root.get("currency"), currency));
    }
    if (from != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get("spendDate"), from));
    }
    if (to != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get("spendDate"), to));
    }

    cq.select(root).where(predicates.toArray(new Predicate[0]))
            .orderBy(cb.desc(root.get("spendDate")));

    return entityManager.createQuery(cq).getResultList();
  }

  public CategoryEntity updateCategory(CategoryEntity category) {
    entityManager.joinTransaction();
    return entityManager.merge(category);
  }

  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    try {
      return Optional.of(
              entityManager.createQuery("select c from CategoryEntity c where c.username =: username and c.name =: name", CategoryEntity.class)
                      .setParameter("username", username)
                      .setParameter("name", name)
                      .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public List<CategoryEntity> allCategories(String username) {
    return entityManager.createQuery(
                    "select c from CategoryEntity c where c.username =: username order by c.name asc",
                    CategoryEntity.class
            )
            .setParameter("username", username)
            .getResultList();
  }

}

