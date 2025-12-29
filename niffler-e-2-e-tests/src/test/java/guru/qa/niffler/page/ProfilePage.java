package guru.qa.niffler.page;

import guru.qa.niffler.config.Config;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {
  //http://127.0.0.1:3000/profile
  public static String url = Config.getInstance().frontUrl() + "profile";

  private final SelenideElement profileLabel = $x("//h2[text()='Profile']");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
  private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
  private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

  private final SelenideElement avatar = $("#image__input").parent().$("img");
  private final SelenideElement userName = $("#username");
  private final SelenideElement nameInput = $("#name");
  private final SelenideElement photoInput = $("input[type='file']");
  private final SelenideElement submitButton = $("button[type='submit']");

  @Nonnull
  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  @Nonnull
  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  @Nonnull
  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  @Nonnull
  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  @Nonnull
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  @Nonnull
  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  @Nonnull
  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }
//===================================

  @Nonnull
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }

  @Nonnull
  public ProfilePage addNewCategory(String newCategory) {
    categoryInput.setValue(newCategory).pressEnter();
    return this;
  }

  @Nonnull
  public ProfilePage checkThatItIsProfilePage() {
    profileLabel.should(visible);
    return this;
  }

  @Nonnull
  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  @Nonnull
  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

}
