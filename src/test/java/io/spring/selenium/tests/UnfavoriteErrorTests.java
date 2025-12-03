package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UnfavoriteErrorTests extends BaseTest {

  private LoginPage loginPage;
  private ArticlePage articlePage;
  private ApiHelper apiHelper;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    loginPage = new LoginPage(driver);
    articlePage = new ArticlePage(driver);
    apiHelper = new ApiHelper(API_URL);
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC021_unfavoriteNonExistentArticleReturns404() {
    createTest(
        "TC-021: Unfavorite non-existent article returns 404 Not Found",
        "Verify that unfavoriting a non-existent article returns 404");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("non-existent-article-slug-12345");
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 404, "Non-existent article should return 404 Not Found");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC022_unauthenticatedUnfavoriteReturns401() {
    createTest(
        "TC-022: Unauthenticated unfavorite request returns 401 Unauthorized",
        "Verify that unfavoriting without authentication returns 401");

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticleWithoutAuth("welcome-to-realworld");
    test.info("Response status: " + response.getStatusCode());

    assertEquals(
        response.getStatusCode(), 401, "Unauthenticated request should return 401 Unauthorized");
  }

  @Test(groups = {"regression", "error"})
  public void TC023_unfavoriteWithExpiredTokenReturns401() {
    createTest(
        "TC-023: Unfavorite with expired token returns 401 Unauthorized",
        "Verify that unfavoriting with an expired JWT token returns 401");

    String expiredToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNjAwMDAwMDAwfQ.expired_signature";

    ApiHelper.ApiResponse response =
        apiHelper.unfavoriteArticleWithToken("welcome-to-realworld", expiredToken);
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 401, "Expired token should return 401 Unauthorized");
  }

  @Test(groups = {"regression", "error"})
  public void TC024_unfavoriteWithInvalidTokenReturns401() {
    createTest(
        "TC-024: Unfavorite with invalid token returns 401 Unauthorized",
        "Verify that unfavoriting with a random invalid token returns 401");

    String invalidToken = "random-invalid-token-string-12345";

    ApiHelper.ApiResponse response =
        apiHelper.unfavoriteArticleWithToken("welcome-to-realworld", invalidToken);
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 401, "Invalid token should return 401 Unauthorized");
  }

  @Test(groups = {"regression", "error"})
  public void TC025_unfavoriteWithMalformedTokenReturns401() {
    createTest(
        "TC-025: Unfavorite with malformed token returns 401 Unauthorized",
        "Verify that unfavoriting with a malformed JWT returns 401");

    String malformedToken = "not.a.valid.jwt.token";

    ApiHelper.ApiResponse response =
        apiHelper.unfavoriteArticleWithToken("welcome-to-realworld", malformedToken);
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 401, "Malformed token should return 401 Unauthorized");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC026_unfavoriteArticleNeverFavoritedGracefulHandling() {
    createTest(
        "TC-026: Unfavorite article never favorited - graceful handling",
        "Verify that unfavoriting an article that was never favorited is handled gracefully");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    apiHelper.unfavoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Response status: " + response.getStatusCode());
    test.info("Response body: " + response.getBody());

    assertTrue(
        response.getStatusCode() == 200 || response.getStatusCode() == 422,
        "Unfavoriting non-favorited article should be handled gracefully");
  }

  @Test(groups = {"regression", "error"})
  public void TC027_unfavoriteAlreadyUnfavoritedArticleIdempotent() {
    createTest(
        "TC-027: Unfavorite already unfavorited article - idempotent behavior",
        "Verify that unfavoriting an already unfavorited article shows idempotent behavior");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    apiHelper.favoriteArticle("welcome-to-realworld");
    apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("First unfavorite completed");

    ApiHelper.ApiResponse secondUnfavorite = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Second unfavorite response: " + secondUnfavorite.getStatusCode());

    assertTrue(
        secondUnfavorite.getStatusCode() == 200 || secondUnfavorite.getStatusCode() == 422,
        "Second unfavorite should be idempotent or return validation error");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void TC028_unfavoriteWithMissingAuthorizationHeader() {
    createTest(
        "TC-028: Unfavorite with missing Authorization header returns 401",
        "Verify that unfavoriting without any auth header returns 401");

    ApiHelper noAuthHelper = new ApiHelper(API_URL);

    ApiHelper.ApiResponse response = noAuthHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Response status: " + response.getStatusCode());

    assertEquals(
        response.getStatusCode(),
        401,
        "Missing Authorization header should return 401 Unauthorized");
  }

  @Test(groups = {"regression", "error"})
  public void TC029_unfavoriteDeletedArticleReturns404() {
    createTest(
        "TC-029: Unfavorite deleted article returns 404",
        "Verify that unfavoriting a deleted article returns 404 Not Found");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response =
        apiHelper.unfavoriteArticle("deleted-article-that-does-not-exist");
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 404, "Deleted article should return 404 Not Found");
  }

  @Test(groups = {"regression", "error"})
  public void TC030_unfavoriteWithWrongHttpMethodPost() {
    createTest(
        "TC-030: Unfavorite with wrong HTTP method (POST instead of DELETE)",
        "Verify that using POST method for unfavorite endpoint behaves differently");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse postResponse =
        apiHelper.post("/articles/welcome-to-realworld/favorite", "", true);
    test.info("POST response status: " + postResponse.getStatusCode());

    ApiHelper.ApiResponse deleteResponse = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("DELETE response status: " + deleteResponse.getStatusCode());

    assertNotEquals(
        postResponse.isFavorited(),
        deleteResponse.isFavorited(),
        "POST and DELETE should have different effects on favorite status");
  }
}
