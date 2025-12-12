package guru.qa.niffler.test;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JdbcTest {
  SpendDbClient spendDbClient = new SpendDbClient();
  UsersDbClient usersDbClient = new UsersDbClient();

  @Test
  public void daotest() {
    SpendJson spendJson = spendDbClient.createSpend(
            new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                            null,
                            "test-dao-name-3",
                            "duck",
                            false
                    ),
                    CurrencyValues.RUB,
                    100.0,
                    "test dao",
                    "duck"
            )
    );
    System.out.println(spendJson);
  }

  @Test
  public void findCategoryByIdTest(){
    // Arrange
    String idString = "72e86a34-b495-4cb1-884b-14b893fd6f8f";//Candy
    UUID id = UUID.fromString(idString);
    // Act
    Optional<CategoryJson> result = spendDbClient.findCategoryById(id);
    // Assert
    assertTrue(result.isPresent());
  }

  @Test
  public void findSpendByIdTest(){
    // Arrange
    String idString = "7c19ff07-eb23-44b6-8039-96a46d7f2930";//duck chocolate
    UUID id = UUID.fromString(idString);
    // Act
    Optional<SpendJson> result = spendDbClient.findSpendById(id);
    // Assert
    assertTrue(result.isPresent());
  }

  @Test
  public void deleteSpendByIdTest(){
    // Arrange
    String idString = "02a94724-d6cf-11f0-b7b0-be6cbb8a0e8e";//duck 22
    UUID id = UUID.fromString(idString);
    // Act
    spendDbClient.deleteSpend(id);
    Optional<SpendJson> result = spendDbClient.findSpendById(id);
    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  public void deleteCategoryByIdTest(){
    // Arrange
    String idString = "55a53e04-d6cc-11f0-a866-be6cbb8a0e8e";//test-dao-name-3 42
    UUID id = UUID.fromString(idString);
    // Act
    spendDbClient.deleteCategory(id);
    Optional<CategoryJson> result = spendDbClient.findCategoryById(id);
    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  public void findUserByIdTest(){
    // Arrange
    String idString = "fc647979-69b0-40f2-a37f-8fa8b64b7351";//duck
    UUID id = UUID.fromString(idString);
    // Act
    Optional<UserJson> result = usersDbClient.findUserById(id);
    System.out.println(result.get());
    // Assert
    assertTrue(result.isPresent());
  }
}
