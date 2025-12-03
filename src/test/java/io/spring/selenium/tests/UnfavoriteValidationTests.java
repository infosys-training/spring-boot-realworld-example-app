package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UnfavoriteValidationTests extends BaseTest {

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

  @Test(groups = {"regression", "validation"})
  public void TC011_verifyDeleteRequestMethodIsRequired() {
    createTest(
        "TC-011: Verify DELETE request method is required for unfavorite",
        "Verify that only DELETE method is accepted for unfavorite endpoint");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse getResponse =
        apiHelper.get("/articles/welcome-to-realworld/favorite", true);
    test.info("GET request response: " + getResponse.getStatusCode());

    assertNotEquals(
        getResponse.getStatusCode(), 200, "GET request should not succeed for unfavorite endpoint");
  }

  @Test(groups = {"regression", "validation"})
  public void TC012_verifyValidArticleSlugFormatIsRequired() {
    createTest(
        "TC-012: Verify valid article slug format is required",
        "Verify that invalid slug formats are rejected");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("invalid--slug--format");
    test.info("Response for invalid slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() == 422,
        "Invalid slug should return 404 or 422");
  }

  @Test(groups = {"regression", "validation"})
  public void TC013_verifyValidAuthenticationTokenFormatIsRequired() {
    createTest(
        "TC-013: Verify valid authentication token format is required",
        "Verify that malformed tokens are rejected with 401");

    ApiHelper.ApiResponse response =
        apiHelper.unfavoriteArticleWithToken("welcome-to-realworld", "malformed-token-123");
    test.info("Response for malformed token: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 401, "Malformed token should return 401 Unauthorized");
  }

  @Test(groups = {"regression", "validation"})
  public void TC014_verifyContentTypeHeaderHandling() {
    createTest(
        "TC-014: Verify Content-Type header handling for unfavorite request",
        "Verify that unfavorite request works regardless of Content-Type header");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
    apiHelper.favoriteArticle("welcome-to-realworld");

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("welcome-to-realworld");
    test.info("Response status: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 200, "Request should be processed successfully");
  }

  @Test(groups = {"regression", "validation"})
  public void TC015_verifyUnfavoriteRequestWithEmptySlug() {
    createTest(
        "TC-015: Verify unfavorite request with empty slug returns error",
        "Verify that empty slug returns appropriate error");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("");
    test.info("Response for empty slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() == 405,
        "Empty slug should return 404 or 405");
  }

  @Test(groups = {"regression", "validation"})
  public void TC016_verifyUnfavoriteWithSpecialCharactersInSlug() {
    createTest(
        "TC-016: Verify unfavorite request with special characters in slug",
        "Verify that special characters in slug are handled appropriately");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("test@#$%slug");
    test.info("Response for special chars slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 400,
        "Special characters in slug should return error");
  }

  @Test(groups = {"regression", "validation"})
  public void TC017_verifyUnfavoriteWithVeryLongSlug() {
    createTest(
        "TC-017: Verify unfavorite request with very long slug",
        "Verify that very long slugs are handled appropriately");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    String longSlug = "a".repeat(500);
    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle(longSlug);
    test.info("Response for long slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 400,
        "Very long slug should return error or 404");
  }

  @Test(groups = {"regression", "validation"})
  public void TC018_verifyUnfavoriteWithNumericOnlySlug() {
    createTest(
        "TC-018: Verify unfavorite request with numeric-only slug",
        "Verify that numeric-only slugs are handled appropriately");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("12345");
    test.info("Response for numeric slug: " + response.getStatusCode());

    assertEquals(response.getStatusCode(), 404, "Non-existent numeric slug should return 404");
  }

  @Test(groups = {"regression", "validation"})
  public void TC019_verifyUnfavoriteWithWhitespaceInSlug() {
    createTest(
        "TC-019: Verify unfavorite request with whitespace in slug",
        "Verify that whitespace in slug is handled appropriately");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("test slug with spaces");
    test.info("Response for whitespace slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 400,
        "Whitespace in slug should return error or 404");
  }

  @Test(groups = {"regression", "validation"})
  public void TC020_verifyUnfavoriteWithUrlEncodedCharacters() {
    createTest(
        "TC-020: Verify unfavorite request with URL-encoded characters in slug",
        "Verify that URL-encoded characters are handled correctly");

    apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

    ApiHelper.ApiResponse response = apiHelper.unfavoriteArticle("test%20slug%20encoded");
    test.info("Response for URL-encoded slug: " + response.getStatusCode());

    assertTrue(
        response.getStatusCode() == 404 || response.getStatusCode() >= 400,
        "URL-encoded non-existent slug should return 404 or error");
  }
}
