package guru.qa.niffler.service;

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

public class SpendDbClient {
  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);
    if (spendEntity.getCategory().getId() == null) {
      CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
      spendEntity.setCategory(categoryEntity);
    }
    return SpendJson.fromEntity(
            spendDao.create(spendEntity)
    );
  }

  @Step("Find category by id")
  public Optional<CategoryJson> findCategoryById(UUID id) {
    Optional<CategoryEntity> ce = categoryDao.findCategoryById(id);
    if (ce.isPresent()) {
      System.out.println(CategoryJson.fromEntity(ce.get()));
      return Optional.of(CategoryJson.fromEntity(ce.get()));
    }else{
      System.out.println("Category not found");
      return Optional.empty();
    }
  }

  public Optional<SpendJson> findSpendById(UUID id){
    Optional<SpendEntity> se = spendDao.findSpendById(id);
    if (se.isPresent()) {
      System.out.println(SpendJson.fromEntity(se.get()));
      return Optional.of(SpendJson.fromEntity(se.get()));
    }else {
      System.out.println("Spend not found");
      return Optional.empty();
    }
  }

  public List<SpendEntity> findSpendsByUsername(String username) {
    return spendDao.findAllByUsername(username);
  }

  public void deleteSpend(UUID id) {
    spendDao.deleteSpend(id);
  }

  public void deleteSpend(SpendEntity spend) {
    spendDao.deleteSpend(spend);
  }

  public void deleteCategory(UUID id) {
    categoryDao.deleteCategory(id);
  }

}
