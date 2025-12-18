package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {
  private static final Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
          CFG.spendJdbcUrl()
  );

  public SpendJson createSpend(SpendJson spend) {
    return jdbcTxTemplate.execute(() -> {
              SpendEntity spendEntity = SpendEntity.fromJson(spend);
              if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
              }
              return SpendJson.fromEntity(
                      spendDao.create(spendEntity)
              );
            }
    );
  }

  @Step("Find category by id")
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return jdbcTxTemplate.execute(() -> {
              Optional<CategoryEntity> ce = categoryDao.findCategoryById(id);
              if (ce.isPresent()) {
                CategoryJson category = CategoryJson.fromEntity(ce.get());
                System.out.println(category);
                return Optional.of(category);
              } else {
                System.out.println("Category not found");
                return Optional.empty();
              }
            }
    );
  }

  public Optional<SpendJson> findSpendById(UUID id) {
    return jdbcTxTemplate.execute(() -> {
              Optional<SpendEntity> se = spendDao.findSpendById(id);
              if (se.isPresent()) {
                System.out.println(SpendJson.fromEntity(se.get()));
                return Optional.of(SpendJson.fromEntity(se.get()));
              } else {
                System.out.println("Spend not found");
                return Optional.empty();
              }
            }
    );
  }

  public List<SpendEntity> findSpendsByUsername(String username) {
    return jdbcTxTemplate.execute(() -> {
              return spendDao.findAllByUsername(username);
            }
    );
  }

  public void deleteSpend(UUID id) {
    jdbcTxTemplate.execute(() -> {
              spendDao.deleteSpend(id);
              return null;
            }
    );
  }

  public void deleteSpend(SpendEntity spend) {
    jdbcTxTemplate.execute(() -> {
              spendDao.deleteSpend(spend);
              return null;
            }
    );
  }

  public void deleteCategory(UUID id) {
    jdbcTxTemplate.execute(()  -> {
              categoryDao.deleteCategory(id);
              return null;
            }
    );
  }

}
