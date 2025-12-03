package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.ErrorPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation tests for Resource Not Found (404) handling. Tests TC-011 to TC-020: Verify proper
 * handling of invalid input formats and boundary conditions.
 */
public class ResourceNotFoundValidationTests extends BaseTest {

  private ArticlePage articlePage;
  private ProfilePage profilePage;
  private ErrorPage errorPage;

  @BeforeMethod
  public void initPages() {
    articlePage = new ArticlePage(driver);
    profilePage = new ProfilePage(driver);
    errorPage = new ErrorPage(driver);
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-011: Verify 404 for article with invalid slug format (special chars)")
  public void testTC011_InvalidSlugSpecialChars() {
    createTest(
        "TC-011: Invalid slug with special characters",
        "Verify 404 Not Found status returned for slug containing @#$%");

    articlePage.navigateToArticle("invalid@slug#with$special%chars");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Invalid slug with special chars should show error or 404 page");

    test.info("Verified 404 handling for invalid slug with special characters");
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-012: Verify 404 for article with empty slug")
  public void testTC012_EmptySlug() {
    createTest("TC-012: Empty article slug", "Verify 404 Not Found or redirect for empty slug");

    errorPage.navigateTo("/article/");
    errorPage.waitForPageLoad();

    String currentUrl = driver.getCurrentUrl();
    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    boolean redirected = !currentUrl.contains("/article/");

    assertTrue(hasError || redirected, "Empty slug should show error, 404, or redirect");

    test.info("Verified handling for empty article slug");
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-013: Verify 404 for profile with invalid username format")
  public void testTC013_InvalidUsernameFormat() {
    createTest(
        "TC-013: Invalid username format",
        "Verify 404 Not Found status returned for username with special chars");

    profilePage.navigateToProfile("invalid@user#name$format");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Invalid username format should show error or 404 page");

    test.info("Verified 404 handling for invalid username format");
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-014: Verify 404 for profile with empty username")
  public void testTC014_EmptyUsername() {
    createTest(
        "TC-014: Empty profile username", "Verify 404 Not Found or redirect for empty username");

    errorPage.navigateTo("/profile/");
    errorPage.waitForPageLoad();

    String currentUrl = driver.getCurrentUrl();
    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    boolean redirected = !currentUrl.contains("/profile/");

    assertTrue(hasError || redirected, "Empty username should show error, 404, or redirect");

    test.info("Verified handling for empty profile username");
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-015: Verify 404 for comment with invalid article slug")
  public void testTC015_CommentInvalidArticleSlug() {
    createTest(
        "TC-015: Comment with invalid article slug",
        "Verify 404 Not Found status returned for comments on invalid article");

    errorPage.navigateTo("/article/invalid@article#slug/comments");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Comments for invalid article slug should show error or 404 page");

    test.info("Verified 404 handling for comments with invalid article slug");
  }

  @Test(
      groups = {"regression", "404", "validation"},
      description = "TC-016: Verify 404 for comment with non-numeric comment ID")
  public void testTC016_NonNumericCommentId() {
    createTest(
        "TC-016: Non-numeric comment ID",
        "Verify 404 Not Found status returned for comment with ID 'abc'");

    errorPage.navigateTo("/article/some-article/comments/abc");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Non-numeric comment ID should show error or 404 page");

    test.info("Verified 404 handling for non-numeric comment ID");
  }

  @Test(
      groups = {"regression", "404", "validation", "security"},
      description = "TC-017: Verify 404 for article slug with SQL injection attempt")
  public void testTC017_SqlInjectionAttempt() {
    createTest(
        "TC-017: SQL injection attempt in slug", "Verify 404 Not Found, no SQL error exposed");

    articlePage.navigateToArticle("'; DROP TABLE articles;--");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "SQL injection attempt should show error or 404 page");

    boolean noSqlError = errorPage.doesNotContainSqlError();
    assertTrue(noSqlError, "SQL injection should not expose SQL errors");

    test.info("Verified 404 handling for SQL injection attempt without exposing SQL errors");
  }

  @Test(
      groups = {"regression", "404", "validation", "security"},
      description = "TC-018: Verify 404 for username with XSS attempt")
  public void testTC018_XssAttempt() {
    createTest("TC-018: XSS attempt in username", "Verify 404 Not Found, XSS not executed");

    profilePage.navigateToProfile("<script>alert(1)</script>");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "XSS attempt should show error or 404 page");

    boolean noXssExecution = errorPage.doesNotContainXssExecution();
    assertTrue(noXssExecution, "XSS should not be executed");

    test.info("Verified 404 handling for XSS attempt without script execution");
  }

  @Test(
      groups = {"regression", "404", "validation", "boundary"},
      description = "TC-019: Verify 404 for extremely long slug (boundary)")
  public void testTC019_ExtremelyLongSlug() {
    createTest(
        "TC-019: Extremely long slug (1000 chars)",
        "Verify 404 Not Found status returned for boundary case");

    StringBuilder longSlug = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longSlug.append("long-slug-");
    }

    articlePage.navigateToArticle(longSlug.toString());
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Extremely long slug should show error or 404 page");

    test.info("Verified 404 handling for extremely long slug (boundary test)");
  }

  @Test(
      groups = {"regression", "404", "validation", "boundary"},
      description = "TC-020: Verify 404 for extremely long username (boundary)")
  public void testTC020_ExtremelyLongUsername() {
    createTest(
        "TC-020: Extremely long username (1000 chars)",
        "Verify 404 Not Found status returned for boundary case");

    StringBuilder longUsername = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longUsername.append("longuser");
    }

    profilePage.navigateToProfile(longUsername.toString());
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Extremely long username should show error or 404 page");

    test.info("Verified 404 handling for extremely long username (boundary test)");
  }
}
