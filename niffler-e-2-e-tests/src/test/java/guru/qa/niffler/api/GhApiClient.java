package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

//3.2 video 21:00
@ParametersAreNonnullByDefault
public class GhApiClient {

  private static final Config CFG = Config.getInstance();
  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";//for user x1y1z64

  private static final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(CFG.ghUrl())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private final GhApi ghApi = retrofit.create(GhApi.class);

  public @Nonnull String issueState(String issueNumber){
    final Response<JsonNode> response;
    try {
      response = ghApi.issue("Bearer" + System.getenv(GH_TOKEN_ENV), issueNumber)
              .execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertEquals(200, response.code());
    return Objects.requireNonNull(response.body()).get("state").asText();
  }

}
