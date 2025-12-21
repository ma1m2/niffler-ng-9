package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbRepoClient {
  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepo = new AuthUserRepositoryJdbc();
  private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
          CFG.authJdbcUrl(),
          CFG.userdataJdbcUrl()
  );

  public UserJson createUserSpringXaTx(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
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
              authUserRepo.create(authUser);

              return UserJson.fromEntity(
                      userdataUserDao.create(UserEntity.fromJson(user)),
                      null
              );
            }
    );
  }
}
