package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

//video 7.2 1:14:29
@ExtendWith(ClientResolver.class)
public class FactoryMethodTest {

  private SpendClient spendClient;
  private UsersClient usersClient;

/*  @ValueSource(strings = {
          "valentin-8"
  })
  @ParameterizedTest*/
  @User
  //@Test
  void springJdbcTest(UserJson user) {
    UserJson newUser = usersClient.createUser(
            user.username(),
            "12345"
    );
    System.out.println(newUser);

    usersClient.addIncomeInvitation(newUser, 1);
    usersClient.addOutcomeInvitation(newUser, 1);
    usersClient.addFriend(newUser, 1);
  }
}
