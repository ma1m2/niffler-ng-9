package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.Databases;
import org.junit.jupiter.api.AfterAll;

public class DatabasesExtension implements SuiteExtension{

  @Override
  public void afterSuite() {
    Databases.closeAllConnections();
  }
}
