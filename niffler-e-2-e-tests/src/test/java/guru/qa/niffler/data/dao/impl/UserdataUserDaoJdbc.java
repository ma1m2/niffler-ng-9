package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoJdbc implements UserdataUserDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
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
      }
    }catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM  \"user\"  WHERE id = ?")) {
        ps.setObject(1, id);
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
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can not find user by ID: " + id, e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
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
      }
    } catch (SQLException e) {
      throw new RuntimeException("Can not find user by username: " + username, e);
    }
    return Optional.empty();
  }

  @Override
  public void delete(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM  \"user\"  WHERE id = ?"
      )) {
        ps.setObject(1, user.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Error deleting user: " + user.getUsername(), e);
    }
  }
}
