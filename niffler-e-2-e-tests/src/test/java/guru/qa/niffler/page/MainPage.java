package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement statComponent = $("#stat");
  private final SelenideElement historySpending = $x("//h2[contains(text(),'History')]");
  private final SelenideElement personalIcon = $("[data-testid='PersonIcon']");
  private final SelenideElement profileLink = $("[href='/profile']");
  private final SelenideElement header = $("#root header");
  private final SelenideElement headerMenu = $("ul[role='menu']");

  public FriendsPage friendsPage() {
    header.$("button").click();
    headerMenu.$$("li").find(text("Friends")).click();
    return new FriendsPage();
  }

  public PeoplePage allPeoplesPage() {
    header.$("button").click();
    headerMenu.$$("li").find(text("All People")).click();
    return new PeoplePage();
  }

  public MainPage checkThatMainPageLoaded() {
    spendingTable.should(visible);
    return this;
  }

  public MainPage checkThatMainPageStatistics() {
    statComponent.should(visible);
    return this;
  }

  public MainPage checkThatMainPageHistorySpending() {
    historySpending.should(visible);
    return this;
  }

  public EditSpendingPage editSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContainsSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description))
        .should(visible);
    return this;
  }

  public ProfilePage navigateToProfile() {
    personalIcon.click();
    profileLink.should(visible).click();
    return new ProfilePage();
  }

}
