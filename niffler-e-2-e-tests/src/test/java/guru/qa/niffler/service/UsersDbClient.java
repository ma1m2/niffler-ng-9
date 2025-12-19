package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UsersDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
  private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
          CFG.authJdbcUrl()
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
          CFG.authJdbcUrl(),
          CFG.userdataJdbcUrl()
  );

  public UserJson createUserJdbc(UserJson user) {
    jdbcTxTemplate.execute(() -> {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(user.username());
      authUser.setPassword(pe.encode("12345"));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);

      AuthUserEntity createdAuthUser = authUserDao.create(authUser);

      AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUserId(createdAuthUser.getId());//createdAuthUser.getId()
                ae.setAuthority(e);
                return ae;
              }
      ).toArray(AuthorityEntity[]::new);

      authAuthorityDao.create(authorityEntities);

      return null;
    });

    return UserJson.fromEntity(userdataUserDao.create(UserEntity.fromJson(user)), null);

  }

  public UserJson createUserSpringXaTx(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
              AuthUserEntity authUser = new AuthUserEntity();
              authUser.setUsername(user.username());
              authUser.setPassword(pe.encode("12345"));
              authUser.setEnabled(true);
              authUser.setAccountNonExpired(true);
              authUser.setAccountNonLocked(true);
              authUser.setCredentialsNonExpired(true);

              AuthUserEntity createdAuthUser = authUserDao.create(authUser);

              AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                      e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setUserId(createdAuthUser.getId());
                        ae.setAuthority(e);
                        return ae;
                      }
              ).toArray(AuthorityEntity[]::new);

              authAuthorityDao.create(authorityEntities);

              return UserJson.fromEntity(
                      userdataUserDao.create(UserEntity.fromJson(user)),
                      null
              );
            }
    );
  }

  public Optional<UserJson> findUserById(UUID id) {
    return xaTransactionTemplate.execute(() -> {
              Optional<UserEntity> ue = userdataUserDao.findById(id);
              if (ue.isPresent()) {
                System.out.println(ue.get());
                return Optional.of(UserJson.fromEntity(ue.get(), null));
              } else {
                System.out.println("User not found by ID" + id.toString());
                return Optional.empty();
              }
            }
    );
  }

  public void deleteUser(UUID idAuth, UUID idUserdata) {
    xaTransactionTemplate.execute(() -> {
      // Удаляем все роли/авторизации пользователя
      authAuthorityDao.delete(idAuth);
      // Удаляем пользователя из auth
      authUserDao.delete(idAuth);
      return null;
    });
    // Удаляем данные пользователя из userdata
    userdataUserDao.delete(idUserdata);
  }
}
