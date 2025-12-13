package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
  private static final Config CFG = Config.getInstance();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
              SpendEntity spendEntity = SpendEntity.fromJson(spend);
              if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
              }
              return SpendJson.fromEntity(
                      new SpendDaoJdbc(connection).create(spendEntity)
              );
            },
            CFG.spendJdbcUrl()
    );
  }

  @Step("Find category by id")
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return transaction(connection -> {
              Optional<CategoryEntity> ce = new CategoryDaoJdbc(connection).findCategoryById(id);
              if (ce.isPresent()) {
                System.out.println(CategoryJson.fromEntity(ce.get()));
                return Optional.of(CategoryJson.fromEntity(ce.get()));
              } else {
                System.out.println("Category not found");
                return Optional.empty();
              }
            },
            CFG.spendJdbcUrl()
    );
  }

  public Optional<SpendJson> findSpendById(UUID id) {
    return transaction(connection -> {
              Optional<SpendEntity> se = new SpendDaoJdbc(connection).findSpendById(id);
              if (se.isPresent()) {
                System.out.println(SpendJson.fromEntity(se.get()));
                return Optional.of(SpendJson.fromEntity(se.get()));
              } else {
                System.out.println("Spend not found");
                return Optional.empty();
              }
            },
            CFG.spendJdbcUrl()
    );
  }

  public List<SpendEntity> findSpendsByUsername(String username) {
    return transaction(connection -> {
              return new SpendDaoJdbc(connection).findAllByUsername(username);
            },
            CFG.spendJdbcUrl()
    );
  }

  public void deleteSpend(UUID id) {
    transaction(connection -> {
              new SpendDaoJdbc(connection).deleteSpend(id);
            },
            CFG.spendJdbcUrl()
    );
  }

  public void deleteSpend(SpendEntity spend) {
    transaction(connection -> {
              new SpendDaoJdbc(connection).deleteSpend(spend);
            },
            CFG.spendJdbcUrl()
    );
  }

  public void deleteCategory(UUID id) {
    transaction(connection -> {
              new CategoryDaoJdbc(connection).deleteCategory(id);
            },
            CFG.spendJdbcUrl()
    );
  }

}
