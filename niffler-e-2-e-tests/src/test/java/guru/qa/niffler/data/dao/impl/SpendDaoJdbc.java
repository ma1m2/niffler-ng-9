package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public SpendEntity create(SpendEntity spend) {
      try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
              "INSERT INTO spend (username, spend_date, currency, amount, description, category_id)" +
                      " VALUES (?, ?, ?, ?, ?, ?)",
              Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, spend.getUsername());
        ps.setDate(2, spend.getSpendDate());
        ps.setString(3, spend.getCurrency().name());
        ps.setDouble(4, spend.getAmount());
        ps.setString(5, spend.getDescription());
        ps.setObject(6, spend.getCategory().getId());

        ps.executeUpdate();

        final UUID generatedKey;
        try (ResultSet rs = ps.getGeneratedKeys()) {//make Select during Insert
          if (rs.next()) {
            generatedKey = rs.getObject("id", UUID.class);
          } else {
            throw new SQLException("Can`t find id in ResultSet");
          }
        }
        spend.setId(generatedKey);
        return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
      try(PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement("SELECT * FROM spend WHERE id = ?")) {
        ps.setObject(1, id);
        ps.execute();

        try (ResultSet rs = ps.getResultSet()) {
          if(rs.next()) {
            SpendEntity se = new SpendEntity();
            se.setId(id);
            se.setUsername(rs.getString("username"));
            se.setSpendDate(rs.getDate("spend_date"));
            se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            se.setAmount(rs.getDouble("amount"));
            se.setDescription(rs.getString("description"));
          // Получаем category_id из ResultSet
          UUID categoryId = (UUID) rs.getObject("category_id");
          // Загружаем CategoryEntity по categoryId (например, через DAO)
          Optional<CategoryEntity> category = new CategoryDaoJdbc().findCategoryById(categoryId);
          category.ifPresent(se::setCategory);
            return Optional.of(se);
          }else {
            return Optional.empty();
          }
        }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    throw new UnsupportedOperationException("The method has not been written yet.");
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
      try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
              "DELETE FROM spend WHERE id = ?")) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteSpend(UUID id) {
      try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
              "DELETE FROM spend WHERE id = ?")) {
        ps.setObject(1, id);
        ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
