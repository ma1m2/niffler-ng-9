package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.Bubble;
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

  @Test
  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ApiLogin
  void categoryDescriptionShouldBeChangedFromTable() {
    final String newDescription = "Обучение Niffler Next Generation";

    new MainPage().getSpendingTable()
            .editSpending("Обучение Advanced 2.0")
            .setNewSpendingDescription(newDescription)
            .saveSpending();

    new MainPage().getSpendingTable()
            .checkTableContains(newDescription);
  }

  @Test
  @User
  @ApiLogin
  void shouldAddNewSpending() {
    final String category = "Friends";
    final int amount = 100;
    final Date currentDate = new Date();
    final String description = RandomDataUtils.randomSentence(3);

    new MainPage()
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

  @Test
  @User
  @ApiLogin
  void shouldNotAddSpendingWithEmptyCategory() {
    new MainPage().getHeader()
            .addSpendingPage()
            .setNewSpendingAmount(100)
            .setNewSpendingDate(new Date())
            .saveSpending()
            .checkFormErrorMessage("Please choose category");
  }

  @Test
  @User
  @ApiLogin
  void shouldNotAddSpendingWithEmptyAmount() {
    new MainPage().getHeader()
            .addSpendingPage()
            .setNewSpendingCategory("Friends")
            .setNewSpendingDate(new Date())
            .saveSpending()
            .checkFormErrorMessage("Amount has to be not less then 0.01");
  }

  @Test
  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ApiLogin
  void deleteSpendingTest() {
    new MainPage().getSpendingTable()
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
  @ApiLogin
  @ScreenShotTest("img/expected-stat.png")//bad-expected-stat.png
  void checkStatComponentTest(BufferedImage expected) {
    new MainPage().getStatComponent()
            .checkBubbles(new Bubble(Color.yellow, "Обучение 79990 ₽"))
            .checkStatisticImage(expected);
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
            .checkStatisticBubblesContains("Обучение 79990 ₽", "Поездки 1000 ₽");
           // .checkBubbles(Color.yellow, Color.green);
  }

  @User(
          categories = {
                  @Category(name = "Traveling"),
                  @Category(name = "Repair", archived = true),
                  @Category(name = "Insure", archived = true)
          },
          spendings = {
                  @Spending(
                          category = "Traveling",
                          description = "В Москву",
                          amount = 9500
                  ),
                  @Spending(
                          category = "Repair",
                          description = "Цемент",
                          amount = 100
                  ),
                  @Spending(
                          category = "Insure",
                          description = "ОСАГО",
                          amount = 3000
                  )
          }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void statComponentShouldDisplayArchivedCategories(BufferedImage expected) {
    new MainPage().getStatComponent()
            .checkBubbles(
                    new Bubble(Color.yellow, "Поездки 9500 ₽"),
                    new Bubble(Color.green, "Archived 3100 ₽")
            )
            .checkStatisticImage(expected);
  }
}