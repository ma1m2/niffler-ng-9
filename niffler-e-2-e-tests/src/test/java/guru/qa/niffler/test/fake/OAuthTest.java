package guru.qa.niffler.test.fake;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

public class OAuthTest {

  private static Config CFG = Config.getInstance();
  AuthApiClient authApiClient = new AuthApiClient();

  @Test//video 11.2 after 26'
  @User(friends = 1)
  @ApiLogin
  public void oauthExtensionTest(@Token String token, UserJson user) {
    System.out.println("### user: " + user);
    System.out.println("### token: " + token);
    Assertions.assertNotNull(token);
  }

  //@Test//video 11.2 before 20' converter-scalars not jackson
  public void oauthTest() {
    String token = authApiClient.login("duck", "12345");
    System.out.println("### token: " + token);
    Assertions.assertNotNull(token);

    Selenide.open(CFG.frontUrl());
    Selenide.localStorage().setItem("id_token", token);
    WebDriverRunner.getWebDriver().manage().addCookie(
            new Cookie(
                    "JSESSIONID",
                    ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
            )
    );
    Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
  }
}
