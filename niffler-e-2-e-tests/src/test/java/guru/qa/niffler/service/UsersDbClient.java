package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public class UsersDbClient {
  private final UserdataUserDao userDao = new UserdataUserDaoJdbc();

  public Optional<UserJson> findUserById(UUID id){
    Optional<UserEntity> ue = userDao.findById(id);
    if (ue.isPresent()) {
      System.out.println(ue.get());
      return Optional.of(UserJson.fromEntity(ue.get(), FriendshipStatus.FRIEND));
    }else {
      System.out.println("User not found by ID" + id.toString());
      return Optional.empty();
    }
  }
}
