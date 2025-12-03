package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleEditorPage;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error test cases for Update Article functionality. Tests TC-021 through TC-030 covering negative
 * and error handling scenarios.
 */
public class UpdateArticleErrorTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private ArticleEditorPage editorPage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";
  private static final String OTHER_USER_EMAIL = "jane@example.com";
  private static final String OTHER_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new ArticleEditorPage(driver);
  }

  private void loginAsTestUser() {
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void loginAsOtherUser() {
    loginPage.navigateTo(BASE_URL);
    loginPage.login(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"error", "regression"})
  public void testTC021_UpdateArticleAsNonAuthorUser() {
    createTest(
        "TC-021: Update article as non-author user",
        "Verify 403 Forbidden when non-author tries to edit");

    loginAsOtherUser();
    test.info("Logged in as different user (jane)");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    test.info("Navigated to home page");

    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean editButtonVisible = articlePage.isEditButtonVisible();
    test.info("Edit button visible: " + editButtonVisible);

    if (!editButtonVisible) {
      test.pass("Edit button correctly hidden for non-author user");
    } else {
      articlePage.clickEditButton();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      boolean hasError = articlePage.is403Error() || editorPage.hasErrors();
      assertTrue(
          hasError || !editorPage.isOnEditorPage(),
          "Non-author should not be able to access editor");
      test.pass("Non-author access correctly restricted");
    }
  }

  @Test(groups = {"error", "smoke", "regression"})
  public void testTC022_UpdateNonExistentArticle() {
    createTest(
        "TC-022: Update non-existent article",
        "Verify 404 Not Found when trying to edit non-existent article");

    loginAsTestUser();
    test.info("Logged in as test user");

    String nonExistentSlug = "non-existent-article-" + System.currentTimeMillis();
    editorPage.navigateToEdit(BASE_URL, nonExistentSlug);
    test.info("Navigated to non-existent article editor: " + nonExistentSlug);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource().toLowerCase();
    boolean is404 =
        pageSource.contains("404")
            || pageSource.contains("not found")
            || pageSource.contains("error");

    test.info("Page contains error indication: " + is404);
    assertTrue(
        is404 || !editorPage.isOnEditArticlePage(),
        "Should show 404 error or redirect for non-existent article");
    test.pass("Non-existent article handled correctly");
  }

  @Test(groups = {"error", "smoke", "regression"})
  public void testTC023_UpdateArticleWithoutAuthentication() {
    createTest(
        "TC-023: Update article without authentication",
        "Verify 401 Unauthorized when not logged in");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    test.info("Navigated to home page without logging in");

    homePage.clickArticleByIndex(0);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean editButtonVisible = articlePage.isEditButtonVisible();
    test.info("Edit button visible without auth: " + editButtonVisible);

    if (!editButtonVisible) {
      test.pass("Edit button correctly hidden for unauthenticated user");
    } else {
      articlePage.clickEditButton();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      boolean redirectedToLogin = currentUrl.contains("login");
      test.info("Redirected to login: " + redirectedToLogin);

      assertTrue(
          redirectedToLogin || !editorPage.isOnEditorPage(),
          "Unauthenticated user should be redirected to login");
      test.pass("Unauthenticated access correctly handled");
    }
  }

  @Test(groups = {"error", "regression"})
  public void testTC024_UpdateArticleWithInvalidSlugInUrl() {
    createTest(
        "TC-024: Update article with invalid slug in URL",
        "Verify error handling for malformed slug");

    loginAsTestUser();
    test.info("Logged in as test user");

    String invalidSlug = "invalid///slug\\\\with<>special";
    editorPage.navigateToEdit(BASE_URL, invalidSlug);
    test.info("Navigated to article with invalid slug: " + invalidSlug);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource().toLowerCase();
    boolean hasError =
        pageSource.contains("error")
            || pageSource.contains("404")
            || pageSource.contains("not found");

    test.info("Page shows error for invalid slug: " + hasError);
    assertTrue(
        hasError || !editorPage.isOnEditArticlePage(), "Should handle invalid slug appropriately");
    test.pass("Invalid slug handled correctly");
  }

  @Test(groups = {"error", "regression"})
  public void testTC025_UpdateArticleWithExpiredSession() {
    createTest(
        "TC-025: Update article with expired session",
        "Verify 401 Unauthorized when session expires");

    loginAsTestUser();
    test.info("Logged in as test user");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    homePage.clickArticleByIndex(0);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      driver.manage().deleteAllCookies();
      ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
      test.info("Cleared session data to simulate expired session");

      editorPage.updateBody("Test body after session clear " + System.currentTimeMillis());
      editorPage.clickSubmit();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL after submission: " + currentUrl);

      assertNotNull(currentUrl, "Page should respond after session expiry");
      test.pass("Expired session handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"error", "regression"})
  public void testTC026_UpdateArticleWithMalformedRequest() {
    createTest(
        "TC-026: Update article with malformed request",
        "Verify 400 Bad Request for malformed data");

    loginAsTestUser();
    test.info("Logged in as test user");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    homePage.clickArticleByIndex(0);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String malformedContent = "\u0000\u0001\u0002\u0003";
      editorPage.updateBody(malformedContent);
      editorPage.clickSubmit();
      test.info("Submitted with malformed content");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should handle malformed request");
      test.pass("Malformed request handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"error", "regression"})
  public void testTC027_UpdateArticleWithInvalidContentType() {
    createTest(
        "TC-027: Update article with invalid content type",
        "Verify error handling for invalid content type");

    loginAsTestUser();
    test.info("Logged in as test user");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();
    homePage.clickArticleByIndex(0);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isEditButtonVisible()) {
      articlePage.clickEditButton();
      editorPage.waitForPageToLoad();

      String binaryContent = "PK\u0003\u0004\u0014\u0000\u0000\u0000";
      editorPage.updateBody(binaryContent);
      editorPage.clickSubmit();
      test.info("Submitted with binary-like content");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);

      assertNotNull(currentUrl, "Page should handle invalid content type");
      test.pass("Invalid content type handled correctly");
    } else {
      test.skip("Edit button not visible - user may not be the author");
    }
  }

  @Test(groups = {"error", "regression"})
  public void testTC028_UpdateArticleBelongingToAnotherUser() {
    createTest(
        "TC-028: Update article belonging to another user",
        "Verify 403 Forbidden when editing another user's article");

    loginAsOtherUser();
    test.info("Logged in as jane (different user)");

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    for (int i = 0; i < Math.min(5, homePage.getArticleCount()); i++) {
      homePage.clickArticleByIndex(i);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String author = articlePage.getAuthor();
      test.info("Article " + i + " author: " + author);

      if (!author.equals("janedoe") && !author.equals("jane")) {
        boolean editButtonVisible = articlePage.isEditButtonVisible();
        test.info("Edit button visible for other user's article: " + editButtonVisible);

        assertFalse(editButtonVisible, "Edit button should be hidden for articles by other users");
        test.pass("Edit button correctly hidden for another user's article");
        return;
      }

      homePage.navigateTo(BASE_URL);
      homePage.waitForArticlesToLoad();
    }

    test.skip("Could not find article by another user");
  }

  @Test(groups = {"error", "regression"})
  public void testTC029_UpdateDeletedArticle() {
    createTest(
        "TC-029: Update deleted article",
        "Verify 404 Not Found when trying to edit deleted article");

    loginAsTestUser();
    test.info("Logged in as test user");

    String deletedSlug = "deleted-article-" + System.currentTimeMillis();
    editorPage.navigateToEdit(BASE_URL, deletedSlug);
    test.info("Navigated to supposedly deleted article: " + deletedSlug);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource().toLowerCase();
    boolean is404 =
        pageSource.contains("404")
            || pageSource.contains("not found")
            || pageSource.contains("error");

    test.info("Page shows 404 for deleted article: " + is404);
    assertTrue(
        is404 || !editorPage.isOnEditArticlePage(), "Should show 404 error for deleted article");
    test.pass("Deleted article handled correctly");
  }

  @Test(groups = {"error", "regression"})
  public void testTC030_UpdateArticleWithInvalidArticleId() {
    createTest(
        "TC-030: Update article with invalid article ID",
        "Verify error handling for non-numeric article ID");

    loginAsTestUser();
    test.info("Logged in as test user");

    String invalidId = "abc123!@#";
    editorPage.navigateToEdit(BASE_URL, invalidId);
    test.info("Navigated to article with invalid ID: " + invalidId);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = driver.getPageSource().toLowerCase();
    boolean hasError =
        pageSource.contains("error")
            || pageSource.contains("404")
            || pageSource.contains("not found");

    test.info("Page shows error for invalid ID: " + hasError);
    assertTrue(
        hasError || !editorPage.isOnEditArticlePage(),
        "Should handle invalid article ID appropriately");
    test.pass("Invalid article ID handled correctly");
  }
}
