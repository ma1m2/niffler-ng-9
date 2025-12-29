package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//video 7.2 1:14:29
@ExtendWith(ClientResolver.class)
public class FactoryMethodTest {

  private SpendClient spendClient;
  private UsersClient usersClient;

  @ValueSource(strings = {
          "valentin-6"
  })
  @ParameterizedTest
  void springJdbcTest(String uname) {
    UserJson user = usersClient.createUser(
            uname,
            "12345"
    );

    usersClient.addIncomeInvitation(user, 1);
    usersClient.addOutcomeInvitation(user, 1);
    usersClient.addFriend(user, 1);
  }
}
