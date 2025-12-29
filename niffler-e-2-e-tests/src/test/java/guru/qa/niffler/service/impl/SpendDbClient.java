package guru.qa.niffler.service.impl;

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
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDbClient  implements SpendClient {
  private static final Config CFG = Config.getInstance();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
          CFG.spendJdbcUrl()
  );

  @Nonnull
  @Override
  public SpendJson createSpend(SpendJson spend) {
    return Objects.requireNonNull(jdbcTxTemplate.execute(() -> {
              SpendEntity spendEntity = SpendEntity.fromJson(spend);
              if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
              }
              return SpendJson.fromEntity(
                      spendDao.create(spendEntity)
              );
            }
    ));
  }

  @Nonnull
  @Step("Find category by id")
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return Objects.requireNonNull(jdbcTxTemplate.execute(() -> {
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
    ));
  }

  @Nonnull
  public Optional<SpendJson> findSpendById(UUID id) {
    return Objects.requireNonNull(jdbcTxTemplate.execute(() -> {
              Optional<SpendEntity> se = spendDao.findSpendById(id);
              if (se.isPresent()) {
                System.out.println(SpendJson.fromEntity(se.get()));
                return Optional.of(SpendJson.fromEntity(se.get()));
              } else {
                System.out.println("Spend not found");
                return Optional.empty();
              }
            }
    ));
  }

  @Nonnull
  public List<SpendEntity> findSpendsByUsername(String username) {
    return Objects.requireNonNull(jdbcTxTemplate.execute(() -> {
              return spendDao.findAllByUsername(username);
            }
    ));
  }

  public void deleteSpend(UUID id) {
    jdbcTxTemplate.execute(() -> {
              spendDao.delete(id);
              return null;
            }
    );
  }

  public void deleteSpend(SpendEntity spend) {
    jdbcTxTemplate.execute(() -> {
              spendDao.delete(spend);
              return null;
            }
    );
  }

  public void deleteCategory(UUID id) {
    jdbcTxTemplate.execute(()  -> {
              categoryDao.delete(id);
              return null;
            }
    );
  }

}
