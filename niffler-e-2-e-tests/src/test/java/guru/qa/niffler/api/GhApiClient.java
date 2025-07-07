package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.SneakyThrows;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

//3.2 video 21:00
public class GhApiClient {

  private static final Config CFG = Config.getInstance();
  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";//for user x1y1z64

  private static final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(CFG.ghUrl())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

  private final GhApi ghApi = retrofit.create(GhApi.class);

  @SneakyThrows
  public String issueState(String issueNumber){
    JsonNode responseBody = ghApi.issue("Bearer" + System.getenv(GH_TOKEN_ENV), issueNumber)
            .execute()
            .body();
    return Objects.requireNonNull(responseBody).get("state").asText();
  }

}
