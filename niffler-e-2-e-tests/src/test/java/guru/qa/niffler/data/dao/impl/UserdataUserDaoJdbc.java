package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

  private static final Config CFG = Config.getInstance();
  private static final String URL = CFG.userdataJdbcUrl();

  @Override
  public UserEntity create(UserEntity user) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
            "INSERT INTO \"user\" (username, currency) VALUES (?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.executeUpdate();
      final UUID generatedUserId;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedUserId = rs.getObject("id", UUID.class);
        } else {
          throw new IllegalStateException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedUserId);
      return user;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
      ps.setObject(1, id);

      ps.execute();
      ResultSet rs = ps.getResultSet();

      if (rs.next()) {
        UserEntity result = new UserEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUsername(rs.getString("username"));
        result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        result.setFirstname(rs.getString("firstname"));
        result.setSurname(rs.getString("surname"));
        result.setPhoto(rs.getBytes("photo"));
        result.setPhotoSmall(rs.getBytes("photo_small"));
        return Optional.of(result);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
            "SELECT * FROM  \"user\"  WHERE username = ?",
            Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          UserEntity user = new UserEntity();
          user.setId(rs.getObject("id", UUID.class));
          user.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          user.setFirstname(rs.getString("firstname"));
          user.setFullname(rs.getString("full_name"));
          user.setPhoto(rs.getBytes("photo"));
          user.setPhotoSmall(rs.getBytes("photo_small"));
          user.setSurname(rs.getString("surname"));
          user.setUsername(rs.getString("username"));
          return Optional.of(user);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can not find user by username: " + username, e);
    }
    return Optional.empty();
  }

  @Override
  public List<UserEntity> findAll() {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
            "SELECT * FROM spend")) {
      ps.execute();
      List<UserEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UserEntity ue = new UserEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setFullname(rs.getString("full_name"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          result.add(ue);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(UserEntity user) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
            "DELETE FROM  \"user\"  WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting user: " + user.getUsername(), e);
    }
  }

  @Override
  public void delete(UUID id) {
    try (PreparedStatement ps = holder(URL).connection().prepareStatement(
            "DELETE FROM  \"user\"  WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting user with id: " + id, e);
    }
  }
}
