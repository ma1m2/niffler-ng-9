package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//video 11.2
public interface AuthApi {

  @GET("oauth2/authorize")
  Call<Void> authorize(@Query("response_type") String responseType,
                       @Query("client_id") String clientId,
                       @Query("scope") String scope,
                       @Query(value = "redirect_uri", encoded = true) String redirectUri,
                       @Query("code_challenge") String codeChallenge,
                       @Query("code_challenge_method") String codeChallengeMethod
  );

  @POST("login")
  @FormUrlEncoded
  Call<String> login(@Field("username") String username,
                     @Field("password") String password,
                     @Field("_csrf") String csrf
  );

  @POST("oauth2/token")
  @FormUrlEncoded
  Call<String> token(@Field("code") String code,
                     @Field(value = "redirect_uri", encoded = true) String redirectUri,
                     @Field("client_id") String clientId,
                     @Field("code_verifier") String codeChallenge,
                     @Field("grant_type") String grantType
  );

  @GET("register")
  Call<Void> requestRegisterForm();

  @POST("register")
  @FormUrlEncoded
  Call<Void> register(@Field("username") String username,
                      @Field("password") String password,
                      @Field("passwordSubmit") String passwordSubmit,
                      @Field("_csrf") String csrf
  );
}
