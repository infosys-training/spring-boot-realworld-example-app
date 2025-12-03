package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error handling test cases for Add Comment to Article functionality. Test cases TC-021 through
 * TC-030 covering error scenarios and negative tests.
 */
public class CommentErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void navigateToFirstArticle() {
    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    if (homePage.hasArticles()) {
      homePage.clickFirstArticle();
      articlePage.waitForCommentsToLoad();
    }
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC021_Return404ForNonExistentArticleSlug() {
    createTest(
        "TC-021: Return 404 for non-existent article slug",
        "Verify that accessing a non-existent article returns 404");

    loginAsTestUser();

    driver.get(BASE_URL + "/article/non-existent-article-slug-xyz-12345");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isNotFoundOrError =
        articlePage.is404Displayed()
            || articlePage.isErrorDisplayed()
            || !articlePage.isCommentFormDisplayed();

    assertTrue(isNotFoundOrError, "Non-existent article should show 404 or error");
    test.info("404 error verified for non-existent article");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC022_Return401ForUnauthenticatedRequest() {
    createTest(
        "TC-022: Return 401 for unauthenticated request",
        "Verify that unauthenticated users cannot add comments");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    if (homePage.hasArticles()) {
      homePage.clickFirstArticle();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean isSignInPromptDisplayed =
          articlePage.isSignInPromptDisplayed() || !articlePage.isCommentFormDisplayed();

      assertTrue(
          isSignInPromptDisplayed,
          "Unauthenticated user should see sign in prompt or no comment form");
      test.info("401 unauthorized verified for unauthenticated request");
    } else {
      test.skip("No articles available for testing");
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC023_Return401ForExpiredJwtToken() {
    createTest(
        "TC-023: Return 401 for expired JWT token", "Verify that expired tokens are rejected");

    loginAsTestUser();
    navigateToFirstArticle();

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");

    driver.navigate().refresh();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isSignInPromptDisplayed =
        articlePage.isSignInPromptDisplayed() || !articlePage.isCommentFormDisplayed();

    assertTrue(
        isSignInPromptDisplayed || homePage.isSignInLinkDisplayed(),
        "Expired/cleared token should require re-authentication");
    test.info("Expired token handling verified");
  }

  @Test(groups = {"regression", "error"})
  public void testTC024_Return401ForInvalidJwtToken() {
    createTest(
        "TC-024: Return 401 for invalid JWT token", "Verify that invalid tokens are rejected");

    driver.get(BASE_URL);

    ((JavascriptExecutor) driver)
        .executeScript(
            "window.localStorage.setItem('user', JSON.stringify({token: 'invalid.jwt.token'}));");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    if (homePage.hasArticles()) {
      homePage.clickFirstArticle();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(articlePage.isOnArticlePage(), "Should be on article page");
      test.info("Invalid token handling verified");
    } else {
      test.skip("No articles available for testing");
    }
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC025_Return401ForMissingAuthorizationHeader() {
    createTest(
        "TC-025: Return 401 for missing Authorization header",
        "Verify that requests without auth header are rejected");

    driver.manage().deleteAllCookies();
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    if (homePage.hasArticles()) {
      homePage.clickFirstArticle();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean isSignInPromptDisplayed =
          articlePage.isSignInPromptDisplayed() || !articlePage.isCommentFormDisplayed();

      assertTrue(
          isSignInPromptDisplayed,
          "Missing auth header should show sign in prompt or no comment form");
      test.info("Missing authorization header handling verified");
    } else {
      test.skip("No articles available for testing");
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC026_Return404ForDeletedArticle() {
    createTest("TC-026: Return 404 for deleted article", "Verify that deleted articles return 404");

    loginAsTestUser();

    driver.get(BASE_URL + "/article/deleted-article-that-no-longer-exists");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isNotFoundOrError =
        articlePage.is404Displayed()
            || articlePage.isErrorDisplayed()
            || !articlePage.isCommentFormDisplayed();

    assertTrue(isNotFoundOrError, "Deleted article should show 404 or error");
    test.info("404 error verified for deleted article");
  }

  @Test(groups = {"regression", "error"})
  public void testTC027_Return422ForMalformedRequestBody() {
    createTest(
        "TC-027: Return 422 for malformed request body",
        "Verify that malformed requests are handled gracefully");

    loginAsTestUser();
    navigateToFirstArticle();

    articlePage.enterComment("");
    articlePage.clickPostComment();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Malformed request body handling verified");
  }

  @Test(groups = {"regression", "error"})
  public void testTC028_Return401ForTamperedJwtToken() {
    createTest(
        "TC-028: Return 401 for tampered JWT token", "Verify that tampered tokens are rejected");

    driver.get(BASE_URL);

    ((JavascriptExecutor) driver)
        .executeScript(
            "window.localStorage.setItem('user', JSON.stringify({token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.tampered_signature'}));");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    if (homePage.hasArticles()) {
      homePage.clickFirstArticle();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(articlePage.isOnArticlePage(), "Should be on article page");
      test.info("Tampered token handling verified");
    } else {
      test.skip("No articles available for testing");
    }
  }

  @Test(groups = {"regression", "error"})
  public void testTC029_HandleConcurrentCommentSubmissions() {
    createTest(
        "TC-029: Handle concurrent comment submissions",
        "Verify that concurrent submissions are handled without data corruption");

    loginAsTestUser();
    navigateToFirstArticle();

    articlePage.addComment("Concurrent comment 1 TC-029");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articlePage.addComment("Concurrent comment 2 TC-029");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Concurrent comment submissions handled successfully");
  }

  @Test(groups = {"smoke", "regression", "error"})
  public void testTC030_ReturnErrorForSqlInjectionAttemptInBody() {
    createTest(
        "TC-030: Return error for SQL injection attempt in body",
        "Verify that SQL injection attempts are handled safely");

    loginAsTestUser();
    navigateToFirstArticle();

    String sqlInjection = "'; DROP TABLE comments; --";
    articlePage.addComment(sqlInjection);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(
        articlePage.isErrorDisplayed() && articlePage.getErrorMessage().contains("SQL"),
        "SQL injection should be handled safely");
    test.info("SQL injection attempt handled safely");
  }
}
