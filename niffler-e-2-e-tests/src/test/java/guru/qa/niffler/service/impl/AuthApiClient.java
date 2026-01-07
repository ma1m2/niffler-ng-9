package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.utils.OAuthUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.nio.charset.StandardCharsets;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;

//video 11.2
public class AuthApiClient extends RestClient {

  private static Config CFG = Config.getInstance();
  private final AuthApi authApi;

  public AuthApiClient() {
    super(CFG.authUrl(), true, ScalarsConverterFactory.create(), HEADERS);
    this.authApi = create(AuthApi.class);
  }

  @SneakyThrows
  public String login(String username, String password) {
    final String codeVerifier = OAuthUtils.generateCodeVerifier();
    final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
    final String redirectUri = CFG.frontUrl() + "authorized";
    final String clientId = "client";

    authApi.authorize(
            "code",
            clientId,
            "openid",
            redirectUri,
            codeChallenge,
            "S256"
    ).execute();

    Response<String> loginResponse = authApi.login(
            username,
            password,
            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
    ).execute();

    String url = loginResponse.raw().request().url().toString();
    String code = StringUtils.substringAfter(url, "code=");

    Response<String> tokenResponse = authApi.token(
            code,
            redirectUri,
            clientId,
            codeVerifier,
            "authorization_code"
    ).execute();

    return new ObjectMapper().readTree(tokenResponse.body().getBytes(StandardCharsets.UTF_8)).get("id_token").asText();
  }
}
