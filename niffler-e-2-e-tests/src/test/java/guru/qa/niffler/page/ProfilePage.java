package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage {
  //http://127.0.0.1:3000/profile
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


  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }
//===================================
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }
  public ProfilePage addNewCategory(String newCategory) {
    categoryInput.setValue(newCategory).pressEnter();
    return this;
  }

  public ProfilePage checkThatItIsProfilePage() {
    profileLabel.should(visible);
    return this;
  }

  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

}
