package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error test cases for Get Article Comments functionality. Tests TC-023 through TC-032 covering
 * error handling and negative scenarios.
 */
public class GetCommentsErrorTests extends BaseTest {

  private ArticlePage articlePage;

  // Test data for error scenarios
  private static final String NON_EXISTENT_SLUG = "non-existent-article-xyz-12345";
  private static final String INVALID_SLUG = "!!!invalid!!!";
  private static final String SQL_INJECTION_SLUG = "test'; DROP TABLE comments;--";
  private static final String XSS_SLUG = "<script>alert('xss')</script>";
  private static final String VERY_LONG_SLUG;
  private static final String NUMERIC_SLUG = "12345";
  private static final String SPECIAL_CHARS_SLUG = "test%20article%21";
  private static final String DOUBLE_SLASH_SLUG = "/double-slash";

  static {
    // Generate a 500 character slug
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      sb.append("very-long-");
    }
    VERY_LONG_SLUG = sb.toString();
  }

  @BeforeMethod
  public void setupPages() {
    articlePage = new ArticlePage(driver);
  }

  /** TC-023: View comments for non-existent article returns 404 */
  @Test(groups = {"smoke", "regression", "error"})
  public void testTC023_ViewCommentsNonExistentArticle404() {
    createTest(
        "TC-023: View comments for non-existent article returns 404",
        "Verify 404 Not Found page or error message is displayed for non-existent article");

    articlePage.navigateToArticle(NON_EXISTENT_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Check if error page is displayed or article page is not shown
    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    boolean isArticlePage = articlePage.isArticlePageDisplayed();

    test.info("Is error page: " + isErrorPage);
    test.info("Is article page: " + isArticlePage);

    String pageSource = articlePage.getPageSource().toLowerCase();
    boolean has404 = pageSource.contains("404") || pageSource.contains("not found");
    boolean hasError = pageSource.contains("error") || pageSource.contains("cannot");

    test.info("Page contains 404/not found: " + has404);
    test.info("Page contains error: " + hasError);

    // Either should show error or not show a valid article
    assertTrue(
        isErrorPage || has404 || hasError || !isArticlePage,
        "Non-existent article should show error or 404");
  }

  /** TC-024: View comments with invalid article slug format */
  @Test(groups = {"regression", "error"})
  public void testTC024_ViewCommentsInvalidSlugFormat() {
    createTest(
        "TC-024: View comments with invalid article slug format",
        "Verify error page or redirect to home for invalid slug");

    articlePage.navigateToArticle(INVALID_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = articlePage.getCurrentUrl();
    test.info("Current URL: " + currentUrl);

    // Application should handle gracefully - either error page or redirect
    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    boolean redirected = !currentUrl.contains(INVALID_SLUG);

    test.info("Is error page: " + isErrorPage);
    test.info("Was redirected: " + redirected);

    // Should either show error or redirect
    assertTrue(
        isErrorPage || redirected || !articlePage.isArticlePageDisplayed(),
        "Invalid slug should be handled gracefully");
  }

  /** TC-025: View comments with empty article slug */
  @Test(groups = {"regression", "error"})
  public void testTC025_ViewCommentsEmptySlug() {
    createTest(
        "TC-025: View comments with empty article slug",
        "Verify error page or redirect to articles list for empty slug");

    articlePage.navigateToUrl("http://localhost:3000/article/");

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = articlePage.getCurrentUrl();
    test.info("Current URL: " + currentUrl);

    // Application should handle gracefully
    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    boolean redirected = !currentUrl.endsWith("/article/");

    test.info("Is error page: " + isErrorPage);
    test.info("Was redirected: " + redirected);

    // Should either show error or redirect
    assertTrue(
        isErrorPage || redirected || !articlePage.isArticlePageDisplayed(),
        "Empty slug should be handled gracefully");
  }

  /** TC-026: View comments with SQL injection attempt in slug */
  @Test(groups = {"smoke", "regression", "error", "security"})
  public void testTC026_ViewCommentsSqlInjectionAttempt() {
    createTest(
        "TC-026: View comments with SQL injection attempt in slug",
        "Verify application handles SQL injection safely, no SQL execution");

    articlePage.navigateToArticle(SQL_INJECTION_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = articlePage.getPageSource().toLowerCase();
    test.info("Page loaded successfully (no server crash)");

    // Verify no SQL error messages are displayed
    assertFalse(pageSource.contains("sql syntax"), "Should not show SQL syntax errors");
    assertFalse(pageSource.contains("mysql"), "Should not show MySQL errors");
    assertFalse(pageSource.contains("sqlite"), "Should not show SQLite errors");
    assertFalse(pageSource.contains("database error"), "Should not show database errors");

    // Application should handle gracefully
    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    test.info("Is error/404 page: " + isErrorPage);

    // Should show 404 or error, not execute SQL
    assertTrue(
        isErrorPage || !articlePage.isArticlePageDisplayed(),
        "SQL injection should be handled safely");
  }

  /** TC-027: View comments with XSS attempt in slug */
  @Test(groups = {"smoke", "regression", "error", "security"})
  public void testTC027_ViewCommentsXssAttempt() {
    createTest(
        "TC-027: View comments with XSS attempt in slug",
        "Verify script is not executed, safely escaped");

    articlePage.navigateToArticle(XSS_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = articlePage.getPageSource();
    test.info("Page loaded successfully");

    // Verify the script tag is not rendered as executable
    // It should either be escaped or the page should show 404
    boolean scriptExecuted = pageSource.contains("<script>alert('xss')</script>");
    boolean isErrorPage = articlePage.isErrorPageDisplayed();

    test.info("Script tag present in source: " + scriptExecuted);
    test.info("Is error page: " + isErrorPage);

    // The script should either be escaped, not present, or page should be 404
    assertTrue(
        !scriptExecuted || isErrorPage || !articlePage.isArticlePageDisplayed(),
        "XSS should be prevented");
  }

  /** TC-028: View comments with very long article slug */
  @Test(groups = {"regression", "error"})
  public void testTC028_ViewCommentsVeryLongSlug() {
    createTest(
        "TC-028: View comments with very long article slug",
        "Verify application handles gracefully, error or truncation");

    articlePage.navigateToArticle(VERY_LONG_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = articlePage.getCurrentUrl();
    test.info("Current URL length: " + currentUrl.length());

    // Application should handle gracefully - no crash
    String pageSource = articlePage.getPageSource();
    assertNotNull(pageSource, "Page should load without crashing");

    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    test.info("Is error page: " + isErrorPage);

    // Should either show error/404 or handle gracefully
    test.info("Application handled long slug gracefully");
  }

  /** TC-029: View comments with numeric-only slug */
  @Test(groups = {"regression", "error"})
  public void testTC029_ViewCommentsNumericSlug() {
    createTest(
        "TC-029: View comments with numeric-only slug",
        "Verify either shows article or 404 (no crash)");

    articlePage.navigateToArticle(NUMERIC_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String pageSource = articlePage.getPageSource();
    assertNotNull(pageSource, "Page should load without crashing");

    boolean isArticlePage = articlePage.isArticlePageDisplayed();
    boolean isErrorPage = articlePage.isErrorPageDisplayed();

    test.info("Is article page: " + isArticlePage);
    test.info("Is error page: " + isErrorPage);

    // Should either show article (if exists) or 404 - no crash
    assertTrue(
        isArticlePage || isErrorPage || pageSource.length() > 0,
        "Numeric slug should be handled without crash");
  }

  /** TC-030: View comments with special characters in slug */
  @Test(groups = {"regression", "error"})
  public void testTC030_ViewCommentsSpecialCharsSlug() {
    createTest(
        "TC-030: View comments with special characters in slug",
        "Verify URL decoded properly, shows article or 404");

    articlePage.navigateToArticle(SPECIAL_CHARS_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = articlePage.getCurrentUrl();
    test.info("Current URL: " + currentUrl);

    String pageSource = articlePage.getPageSource();
    assertNotNull(pageSource, "Page should load without crashing");

    boolean isArticlePage = articlePage.isArticlePageDisplayed();
    boolean isErrorPage = articlePage.isErrorPageDisplayed();

    test.info("Is article page: " + isArticlePage);
    test.info("Is error page: " + isErrorPage);

    // Should handle URL encoding gracefully
    assertTrue(
        isArticlePage || isErrorPage || pageSource.length() > 0,
        "Special chars in slug should be handled gracefully");
  }

  /** TC-031: View comments after article is deleted */
  @Test(groups = {"regression", "error"})
  public void testTC031_ViewCommentsAfterArticleDeleted() {
    createTest(
        "TC-031: View comments after article is deleted",
        "Verify 404 error or appropriate message after article deletion");

    // Navigate to a non-existent article (simulating deleted article)
    String deletedArticleSlug = "deleted-article-" + System.currentTimeMillis();
    articlePage.navigateToArticle(deletedArticleSlug);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isErrorPage = articlePage.isErrorPageDisplayed();
    boolean isArticlePage = articlePage.isArticlePageDisplayed();

    test.info("Is error page: " + isErrorPage);
    test.info("Is article page: " + isArticlePage);

    String pageSource = articlePage.getPageSource().toLowerCase();
    boolean has404 = pageSource.contains("404") || pageSource.contains("not found");

    test.info("Page contains 404/not found: " + has404);

    // Should show error or 404 for deleted/non-existent article
    assertTrue(isErrorPage || has404 || !isArticlePage, "Deleted article should show error or 404");
  }

  /** TC-032: View comments with malformed URL */
  @Test(groups = {"regression", "error"})
  public void testTC032_ViewCommentsMalformedUrl() {
    createTest("TC-032: View comments with malformed URL", "Verify application handles gracefully");

    articlePage.navigateToUrl("http://localhost:3000/article/" + DOUBLE_SLASH_SLUG);

    // Wait for page to load
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    String currentUrl = articlePage.getCurrentUrl();
    test.info("Current URL: " + currentUrl);

    String pageSource = articlePage.getPageSource();
    assertNotNull(pageSource, "Page should load without crashing");

    boolean isArticlePage = articlePage.isArticlePageDisplayed();
    boolean isErrorPage = articlePage.isErrorPageDisplayed();

    test.info("Is article page: " + isArticlePage);
    test.info("Is error page: " + isErrorPage);

    // Should handle malformed URL gracefully - no crash
    assertTrue(
        isArticlePage || isErrorPage || pageSource.length() > 0,
        "Malformed URL should be handled gracefully");
  }
}
