package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.UUID;

public interface AuthAuthorityDao {
  void create(AuthorityEntity... authority);

  void delete(UUID user_id);
}
