package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisableByIssue;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @DisableByIssue("5")//4 closed, 5-open
  @DisplayName("Main Page is displayed after login")
  public void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .checkThatMainPageLoaded();
  }

  @Test
  @DisableByIssue("4")//4 closed, 5-open
  @DisplayName("User Stay On Login Page After Login With Bad Credentials")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(){
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .fillLoginPage(randomUsername(), "123456");
    String errorMessage = new LoginPage().getErrorMessage();

    Assertions.assertEquals("Bad credentials", errorMessage);
  }

}
