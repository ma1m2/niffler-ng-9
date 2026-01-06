package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingTest {

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 89990
          )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getSpendingTable()
            .editSpending("Обучение Advanced 2.0")
            .setNewSpendingDescription(newDescription)
            .saveSpending();

    new MainPage().getSpendingTable()
            .checkTableContains(newDescription);
  }

  @User
  @Test
  void shouldAddNewSpending(UserJson user) {
    String category = "Friends";
    int amount = 100;
    Date currentDate = new Date();
    String description = RandomDataUtils.randomSentence(3);

    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getHeader()
            .addSpendingPage()
            .setNewSpendingCategory(category)
            .setNewSpendingAmount(amount)
            .setNewSpendingDate(currentDate)
            .setNewSpendingDescription(description)
            .saveSpending()
            .checkAlert("New spending is successfully created");

    new MainPage().getSpendingTable()
            .checkTableContains(description);
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getHeader()
            .addSpendingPage()
            .setNewSpendingAmount(100)
            .setNewSpendingDate(new Date())
            .saveSpending()
            .checkFormErrorMessage("Please choose category");
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getHeader()
            .addSpendingPage()
            .setNewSpendingCategory("Friends")
            .setNewSpendingDate(new Date())
            .saveSpending()
            .checkFormErrorMessage("Amount has to be not less then 0.01");
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 89990
          )
  )
  @Test
  void deleteSpendingTest(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getSpendingTable()
            .deleteSpending("Обучение Advanced 2.0")
            .checkTableSize(0);
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentOldTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    Selenide.sleep(3000);

    BufferedImage actual = ImageIO.read($("canvas[role='img']").screenshot());
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected-stat.png")//bad-expected-stat.png
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Обучение 79990 ₽")
            .checkStatisticImage(expected)
            .checkBubbles(Color.yellow);
  }

  @User(
          spendings = {
                  @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990),
                  @Spending(
                  category = "Поездки",
                  description = "В Москву",
                  amount = 1000)
          }
  )
  @Test
  void checkBubbleStatComponentTest(@NotNull UserJson user){
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Обучение 79990 ₽", "Поездки 1000 ₽")
            .checkBubbles(Color.yellow, Color.green);
  }

  @User(//username doesn't save in category table, and error "exist category"
          //everytime should be new category
          categories = {
                  @Category(name = "Trip"),
                  @Category(name = "Repairing", archived = true),
                  @Category(name = "Insurance", archived = true)
          },
          spendings = {
                  @Spending(
                          category = "Trip",
                          description = "В Москву",
                          amount = 9500
                  ),
                  @Spending(
                          category = "Repairing",
                          description = "Цемент",
                          amount = 100
                  ),
                  @Spending(
                          category = "Insurance",
                          description = "ОСАГО",
                          amount = 3000
                  )
          }
  )
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
    System.out.println("## " + user.username());
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .getStatComponent()
            .checkStatisticBubblesContains("Trip 9500 ₽", "Archived 3100 ₽")
            .checkStatisticImage(expected);
  }
}