package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//video 6.1-6.2
@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {
  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepo = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository userdataUserRepo = new UserdataUserRepositoryHibernate();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
          CFG.authJdbcUrl(),
          CFG.userdataJdbcUrl()
  );

  @Nonnull
  @Override
  @Step("Create user using SQL")
  public UserJson createUser(String username, String password) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
              AuthUserEntity authUser = authUserEntity(username, password);
              authUserRepo.create(authUser);

              return UserJson.fromEntity(
                      userdataUserRepo.create(userEntity(username)),
                      null
              );
            }
    ));
  }

  @Nonnull
  public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepo.findById(targetUser.id()).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
                  String username = RandomDataUtils.randomUsername();
                  AuthUserEntity authUser = authUserEntity(username, "12345");
                  authUserRepo.create(authUser);
                  UserEntity addressee = userdataUserRepo.create(userEntity(username));
                  userdataUserRepo.addIncomeInvitation(targetEntity, addressee);
                  result.add(UserJson.fromEntity(
                          addressee,
                          FriendshipStatus.INVITE_RECEIVED
                  ));
                  return null;
                }
        );
      }
    }
    return result;
  }

  @Nonnull
  public List<UserJson>  addOutcomeInvitation(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepo.findById(targetUser.id()).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
                  String username = RandomDataUtils.randomUsername();
                  AuthUserEntity authUser = authUserEntity(username, "12345");
                  authUserRepo.create(authUser);
                  UserEntity addressee = userdataUserRepo.create(userEntity(username));
                  userdataUserRepo.addOutcomeInvitation(targetEntity, addressee);
                  result.add(UserJson.fromEntity(
                          addressee,
                          FriendshipStatus.INVITE_RECEIVED
                  ));
                  return null;
                }
        );
      }
    }
    return result;
  }

  @Nonnull
  public List<UserJson>  addFriend(UserJson targetUser, int count) {
    final List<UserJson> result = new ArrayList<>();
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepo.findById(targetUser.id()).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
                  String username = RandomDataUtils.randomUsername();
                  AuthUserEntity authUser = authUserEntity(username, "12345");
                  authUserRepo.create(authUser);
                  UserEntity addressee = userdataUserRepo.create(userEntity(username));
                  userdataUserRepo.addFriend(targetEntity, addressee);
                  result.add(UserJson.fromEntity(
                          addressee,
                          FriendshipStatus.FRIEND
                  ));
                  return null;
                }
        );
      }
    }
    return result;
  }

  @Nonnull
  public UserJson createUserSpringXaTx(UserJson user) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
              AuthUserEntity authUser = authUserEntity(user);
              authUserRepo.create(authUser);

              return UserJson.fromEntity(
                      userdataUserRepo.create(UserEntity.fromJson(user)),
                      null
              );
            }
    ));
  }

  @Nonnull
  private static UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  private @Nonnull AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
    ).toList());
    return authUser;
  }

  private @Nonnull AuthUserEntity authUserEntity(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
    ).toList());
    return authUser;
  }
}
