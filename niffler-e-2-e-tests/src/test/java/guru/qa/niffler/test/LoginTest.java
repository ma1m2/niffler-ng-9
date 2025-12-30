package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

  @User
  @DisableByIssue("5")//4 closed, 5-open
  @Test
  @DisplayName("Main Page is displayed after login")
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage())
            .checkThatPageLoaded();
  }

  @Test
  @DisableByIssue("4")//4 closed, 5-open
  @DisplayName("User Stay On Login Page After Login With Bad Credentials")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(randomUsername(), "BAD")
            .submit(new LoginPage())
            .checkError("Bad credentials");
  }

}
