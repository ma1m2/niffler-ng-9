package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import guru.qa.niffler.service.RestClient;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

//3.2 video 21:00
@ParametersAreNonnullByDefault
public class GhApiClient extends RestClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";//for user x1y1z64

  private final GhApi ghApi;

  public GhApiClient() {
    super(CFG.ghUrl());
    this.ghApi = create(GhApi.class);
  }

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
