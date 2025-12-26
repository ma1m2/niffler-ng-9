package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@WebTest
public class SpendingTest {

  private static final Config CFG = Config.getInstance();

  @User(
          spendings = @Spending(
                  amount = 89990.00,
                  description = "Advanced 9 поток!",
                  category = "Обучение"
          )
  )
  //@DisableByIssue("5")//4 closed, 5-open
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    System.out.println(user.toString());
    final SpendJson spend = user.testData().spendings().getFirst();
    final String newDescription = ":)";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatMainPageLoaded()
            .editSpending(spend.description())
            .setNewSpendingDescription(newDescription)
            .save()
            .checkThatTableContainsSpending(newDescription);
  }

  @User(
          username = "duck",
          spendings = @Spending(
                  amount = 89990.00,
                  description = "Advanced 9 поток!",
                  category = "Обучение"
          )
  )
  @Test
  void creatSpendingWithHardUsername(SpendJson[] spendJsons) {
    System.out.println(Arrays.toString(spendJsons));
  }
}
