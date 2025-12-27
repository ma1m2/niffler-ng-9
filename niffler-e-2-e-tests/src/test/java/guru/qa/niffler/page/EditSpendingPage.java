package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitButton = $("#save");

  @Nonnull
  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  @Nonnull
  public MainPage save() {
    submitButton.click();
    return new MainPage();
  }
}
