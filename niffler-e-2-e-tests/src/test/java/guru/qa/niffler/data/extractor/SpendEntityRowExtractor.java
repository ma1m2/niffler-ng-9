package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpendEntityRowExtractor implements ResultSetExtractor<List<SpendEntity>> {

  public static final SpendEntityRowExtractor instance = new SpendEntityRowExtractor();

  private SpendEntityRowExtractor() {
  }

  @Override
  public List<SpendEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
    List<SpendEntity> result = new ArrayList<>();

    while (rs.next()) {
      SpendEntity spend = new SpendEntity();
      spend.setId(rs.getObject("id", UUID.class));
      spend.setUsername(rs.getString("username"));
      spend.setSpendDate(rs.getDate("spend_date"));
      spend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
      spend.setAmount(rs.getDouble("amount"));
      spend.setDescription(rs.getString("description"));

      CategoryEntity category = new CategoryEntity();
      category.setId(rs.getObject("category_id", UUID.class));
      category.setUsername(rs.getString("username"));
      category.setName(rs.getString("category_name"));
      category.setArchived(rs.getBoolean("category_archived"));

      spend.setCategory(category);
      result.add(spend);
    }
    return result;
  }

}
