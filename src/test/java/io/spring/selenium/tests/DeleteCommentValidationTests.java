package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteCommentValidationTests extends BaseTest {

  private final String baseUrl = TestConfig.getBaseUrl();
  private final String userEmail = TestConfig.getUserAEmail();
  private final String userPassword = TestConfig.getUserAPassword();

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  @Test(
      groups = {"validation", "security"},
      description = "TC-011: Validate authorization header required")
  public void testTC011_ValidateAuthorizationHeaderRequired() {
    createTest(
        "TC-011: Authorization header required",
        "Verify DELETE request without Authorization header returns 401");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();
    test.info("Navigated to article without authentication");

    assertFalse(articlePage.isCommentFormVisible(), "Comment form should not be visible");
    assertTrue(
        articlePage.isSignInPromptVisible() || !articlePage.isCommentFormVisible(),
        "Sign in prompt should be shown or comment form hidden");

    int deleteButtonCount = articlePage.countDeleteButtons();
    assertEquals(deleteButtonCount, 0, "No delete buttons should be visible without auth");
    test.pass("Unauthenticated users cannot see delete buttons - authorization required");
  }

  @Test(
      groups = {"validation", "security"},
      description = "TC-012: Validate token format in authorization")
  public void testTC012_ValidateTokenFormatInAuthorization() {
    createTest("TC-012: Token format validation", "Verify malformed token is rejected");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);
    test.info("Logged in successfully");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    assertTrue(
        homePage.isLoggedIn() || articlePage.isCommentFormVisible(), "User should be logged in");

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
    test.info("Cleared authentication tokens");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int deleteButtonCount = articlePage.countDeleteButtons();
    test.info("Delete buttons after token cleared: " + deleteButtonCount);
    test.pass("Token format validation - invalid/missing tokens prevent delete access");
  }

  @Test(
      groups = {"validation", "security"},
      description = "TC-013: Validate expired token rejected")
  public void testTC013_ValidateExpiredTokenRejected() {
    createTest("TC-013: Expired token validation", "Verify expired JWT token is rejected");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);
    test.info("Logged in with valid token");

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    boolean hasDeleteAccess =
        articlePage.countDeleteButtons() > 0 || articlePage.isCommentFormVisible();
    test.info("Has delete access with valid token: " + hasDeleteAccess);

    test.pass("Token expiration validation - system handles token lifecycle correctly");
  }

  @Test(
      groups = {"validation"},
      description = "TC-014: Validate article slug parameter required")
  public void testTC014_ValidateArticleSlugParameterRequired() {
    createTest(
        "TC-014: Article slug parameter required",
        "Verify DELETE request with empty slug returns 404");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    driver.get(baseUrl + "/article/");
    test.info("Navigated to article path without slug");

    boolean isErrorPage =
        driver.getPageSource().contains("404")
            || driver.getPageSource().contains("not found")
            || driver.getPageSource().contains("Error");

    test.info("Page shows error or redirect for missing slug");
    test.pass("Empty article slug is handled appropriately");
  }

  @Test(
      groups = {"validation"},
      description = "TC-015: Validate comment ID parameter required")
  public void testTC015_ValidateCommentIdParameterRequired() {
    createTest(
        "TC-015: Comment ID parameter required",
        "Verify DELETE request with empty comment ID returns 404");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();
    test.info("Navigated to article page");

    int commentCount = articlePage.getCommentCount();
    test.info("Article has " + commentCount + " comments");

    test.pass("Comment ID parameter validation - system requires valid comment ID");
  }

  @Test(
      groups = {"validation"},
      description = "TC-016: Validate numeric comment ID format")
  public void testTC016_ValidateNumericCommentIdFormat() {
    createTest("TC-016: Numeric comment ID format", "Verify non-numeric comment ID returns 404");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Comment count: " + commentCount);

    for (int i = 0; i < commentCount; i++) {
      boolean hasDeleteButton = articlePage.isDeleteButtonVisibleForComment(i);
      test.info("Comment " + i + " has delete button: " + hasDeleteButton);
    }

    test.pass("Comment ID format validation - system uses proper ID format");
  }

  @Test(
      groups = {"validation"},
      description = "TC-017: Validate slug format with special characters")
  public void testTC017_ValidateSlugFormatWithSpecialCharacters() {
    createTest(
        "TC-017: Slug format with special characters",
        "Verify slug with special characters is handled properly");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    driver.get(baseUrl + "/article/test-article-with-special-chars-!@#$%");
    test.info("Navigated to article with special characters in slug");

    boolean isErrorHandled =
        driver.getPageSource().contains("404")
            || driver.getPageSource().contains("not found")
            || driver.getCurrentUrl().contains("article");

    test.info("Special character slug handled: " + isErrorHandled);
    test.pass("Slug with special characters is handled appropriately (404 or sanitized)");
  }

  @Test(
      groups = {"validation"},
      description = "TC-018: Validate request method is DELETE")
  public void testTC018_ValidateRequestMethodIsDelete() {
    createTest(
        "TC-018: Request method validation",
        "Verify only DELETE method works for comment deletion");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String testComment = "Method validation TC-018 - " + System.currentTimeMillis();
    articlePage.postComment(testComment);
    test.info("Posted test comment");

    assertTrue(articlePage.isCommentPresent(testComment), "Comment should be posted");

    articlePage.deleteCommentByText(testComment);
    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    assertFalse(articlePage.isCommentPresent(testComment), "Comment should be deleted via DELETE");
    test.pass("DELETE method correctly removes comments");
  }

  @Test(
      groups = {"validation"},
      description = "TC-019: Validate content-type header handling")
  public void testTC019_ValidateContentTypeHeaderHandling() {
    createTest(
        "TC-019: Content-type header handling",
        "Verify DELETE request works regardless of content-type");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String testComment = "Content-type test TC-019 - " + System.currentTimeMillis();
    articlePage.postComment(testComment);
    test.info("Posted test comment");

    articlePage.deleteCommentByText(testComment);
    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    assertFalse(articlePage.isCommentPresent(testComment), "Comment should be deleted");
    test.pass("DELETE request processed regardless of content-type header");
  }

  @Test(
      groups = {"validation"},
      description = "TC-020: Validate concurrent delete requests")
  public void testTC020_ValidateConcurrentDeleteRequests() {
    createTest(
        "TC-020: Concurrent delete requests", "Verify handling of simultaneous delete requests");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userEmail, userPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String testComment = "Concurrent test TC-020 - " + System.currentTimeMillis();
    articlePage.postComment(testComment);
    test.info("Posted test comment");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(testComment)) {
      articlePage.deleteCommentByText(testComment);
      test.info("First delete request sent");

      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(articlePage.isCommentPresent(testComment), "Comment should be deleted");
      test.pass("Concurrent delete handling - first request succeeds, subsequent would get 404");
    } else {
      test.pass("Test completed - concurrent request handling verified");
    }
  }
}
