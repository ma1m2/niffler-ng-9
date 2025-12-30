package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegisterTest {

  @Test
  @DisplayName("Register new user")
  void shouldRegisterNewUser() {
    String newUsername = randomUsername();
    String password = "12345";
    Selenide.open(LoginPage.URL, LoginPage.class)
            .doRegister()
            .fillRegisterPage(newUsername, password, password)
            .successSubmit()
            .fillLoginPage(newUsername, password)
            .submit(new MainPage())
            .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Can not register user with the same username")
  void shouldNotRegisterUserWithExistingUsername() {
    String existingUsername = "duck";
    String password = "12345";

    LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
    loginPage.doRegister()
            .fillRegisterPage(existingUsername, password, password)
            .errorSubmit();
    loginPage.checkError("Username `" + existingUsername + "` already exists");
  }

  @Test
  @DisplayName("Passwords should be equal")
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String newUsername = randomUsername();
    String password = "12345";

    LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
    loginPage.doRegister()
            .fillRegisterPage(newUsername, password, "bad password submit")
            .errorSubmit();
    loginPage.checkError("Passwords should be equal");
  }

}
