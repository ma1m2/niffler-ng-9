package guru.qa.niffler.test;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@Disabled
public class JdbcTest {

  SpendDbClient spendDbClient = new SpendDbClient();
  UsersDbClient usersDbClient = new UsersDbClient();

  @Test
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();

    UserJson user = usersDbClient.createUserSpringXaTx(
            new UserJson(
                    null,
                    "valentin",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }
/*
  @Test
  void xaTxTest() {
    UserJson user = usersDbClient.createUser(
            new UserJson(
                    null,
                    "valentin-4",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
  }*/

  @DisplayName("Transaction saves us from inconsistency DB")//if username null
  @Test
  public void tx_test() {
    SpendJson spendJson = spendDbClient.createSpend(
            new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                            null,
                            "test-tx-name",
                            "anna",
                            false
                    ),
                    CurrencyValues.RUB,
                    100.0,
                    "test tx",
                    "anna"
            )
    );
    System.out.println(spendJson);
  }

  @Test
  public void daotest() {
    SpendJson spendJson = spendDbClient.createSpend(
            new SpendJson(
                    null,
                    new Date(),
                    new CategoryJson(
                            null,
                            "test-dao-name",
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
  public void findCategoryByIdTest() {
    // Arrange
    String idString = "72e86a34-b495-4cb1-884b-14b893fd6f8f";//Candy
    UUID id = UUID.fromString(idString);
    // Act
    Optional<CategoryJson> result = spendDbClient.findCategoryById(id);
    // Assert
    assertTrue(result.isPresent());
  }

  @Test
  public void findSpendByIdTest() {
    // Arrange
    String idString = "7c19ff07-eb23-44b6-8039-96a46d7f2930";//duck chocolate
    UUID id = UUID.fromString(idString);
    // Act
    Optional<SpendJson> result = spendDbClient.findSpendById(id);
    // Assert
    assertTrue(result.isPresent());
  }

  @Test
  public void deleteSpendByIdTest() {
    // Arrange
    String idString = "02348182-d8fa-11f0-8766-364f1644149f";//duck 22
    UUID id = UUID.fromString(idString);
    // Act
    spendDbClient.deleteSpend(id);
    Optional<SpendJson> result = spendDbClient.findSpendById(id);
    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  public void deleteCategoryByIdTest() {
    // Arrange
    String idString = "022fb5ee-d8fa-11f0-8766-364f1644149f";//test-tx-name 39
    UUID id = UUID.fromString(idString);
    // Act
    spendDbClient.deleteCategory(id);
    Optional<CategoryJson> result = spendDbClient.findCategoryById(id);
    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  public void findUserByIdTest() {
    // Arrange
    String idString = "69c0c92e-dcbe-11f0-b051-666764dfeab4";//barsik
    UUID id = UUID.fromString(idString);
    // Act
    Optional<UserJson> result = usersDbClient.findUserById(id);
    // Assert
    assertTrue(result.isPresent());
    System.out.println(result.get());
  }

  //@Test
  public void deleteUserByIdTest() {
    // Arrange
    String idAuth = "f425c4a0-dccf-11f0-8e39-666764dfeab4";
    String idUserdata = "f43afeb0-dccf-11f0-8f20-666764dfeab4";
    UUID idUd = UUID.fromString(idUserdata);
    UUID idAu = UUID.fromString(idAuth);
    // Act
    usersDbClient.deleteUser(idAu, idUd);

/*    Optional<UserJson> result = usersDbClient.findUserById(idUd);
    // Assert
    assertTrue(result.isEmpty());*/
  }
}
