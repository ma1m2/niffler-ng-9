package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement registerButton = $("a#register-button");
  private final SelenideElement errorMessage = $("p.form__error");


  @Nonnull
  @Step("Do login")
  public MainPage successLogin(String username, String password) {
    fillLoginPage(username, password);
    return new MainPage();
  }

  @Step("Fill login form")
  public void fillLoginPage(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
  }

  @Nonnull
  @Step("Click Sign Up button")
  public RegisterPage doRegister(){
    registerButton.click();
    return new RegisterPage();
  }

  @Nonnull
  @Step("Read error message")
  public String getErrorMessage() {
    return errorMessage.getText(); //Bad credentials
  }
}
