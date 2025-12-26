package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.EMPTY;

@WebTest
public class FriendsTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @User(
          friends = 1
  )
  void friendShouldBePresentInFriendsTable(UserJson user) {
    final UserJson friend = user.testData().friends().getFirst();
    System.out.println(user + "\n" + friend);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatMainPageLoaded()
            .friendsPage()
            .checkExistingFriends(friend.username());
  }

  @Test
  @User()
  void friendsTableShouldBeEmptyForNewUser(UserJson user) {
    System.out.println(user);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatMainPageLoaded()
            .friendsPage()
            .checkNoExistingFriends();
  }

  @Test
  @User(
          incomeInvitations = 1
  )
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    final UserJson income = user.testData().incomeInvitations().getFirst();
    System.out.println(user + "\n" + income);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatMainPageLoaded()
            .friendsPage()
            .checkExistingInvitations(income.username());
  }

  @Test
  @User(
          outcomeInvitations = 1
  )
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    final UserJson outcome = user.testData().outcomeInvitations().getFirst();
    System.out.println(user + "\n" + outcome);

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .checkThatMainPageLoaded()
            .allPeoplesPage()
            .checkInvitationSentToUser(outcome.username());
  }

}
