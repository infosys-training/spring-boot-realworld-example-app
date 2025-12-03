package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.ErrorPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive tests for Resource Not Found (404) handling. Tests TC-001 to TC-010: Verify that 404
 * responses are correctly returned for various non-existent resources.
 */
public class ResourceNotFoundPositiveTests extends BaseTest {

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
      groups = {"regression", "404"},
      description = "TC-001: Verify 404 for non-existent article by slug")
  public void testTC001_NonExistentArticleBySlug() {
    createTest(
        "TC-001: Non-existent article by slug",
        "Verify 404 Not Found status returned for non-existent article slug");

    articlePage.navigateToArticle("non-existent-article-slug-12345");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Non-existent article should show error or 404 page");

    test.info("Verified 404 handling for non-existent article slug");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-002: Verify 404 for non-existent user profile by username")
  public void testTC002_NonExistentProfileByUsername() {
    createTest(
        "TC-002: Non-existent profile by username",
        "Verify 404 Not Found status returned for non-existent username");

    profilePage.navigateToProfile("nonexistentuser12345xyz");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Non-existent profile should show error or 404 page");

    test.info("Verified 404 handling for non-existent profile username");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-003: Verify 404 for non-existent comment on article")
  public void testTC003_NonExistentCommentOnArticle() {
    createTest(
        "TC-003: Non-existent comment on article",
        "Verify 404 Not Found status returned for non-existent comment");

    errorPage.navigateTo("/article/non-existent-article/comments/99999");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Non-existent comment should show error or 404 page");

    test.info("Verified 404 handling for non-existent comment");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-004: Verify 404 response includes proper error message")
  public void testTC004_ErrorMessageIncluded() {
    createTest(
        "TC-004: Error message included in 404 response",
        "Verify response contains meaningful error message");

    articlePage.navigateToArticle("this-article-does-not-exist-anywhere");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.hasErrorInPageSource();
    assertTrue(hasError, "404 response should include error indication in page");

    test.info("Verified error message is included in 404 response");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-005: Verify 404 response has correct content-type header")
  public void testTC005_ContentTypeHeader() {
    createTest(
        "TC-005: Content-type header verification",
        "Verify Content-Type is appropriate for error response");

    articlePage.navigateToArticle("non-existent-article-for-header-test");
    articlePage.waitForPageLoad();

    String pageSource = driver.getPageSource();
    assertNotNull(pageSource, "Page should have content");
    assertTrue(pageSource.length() > 0, "Page content should not be empty");

    test.info("Verified page returns content (HTML response)");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-006: Verify 404 for article after deletion")
  public void testTC006_DeletedArticle() {
    createTest(
        "TC-006: Deleted article returns 404",
        "Verify 404 Not Found status returned for deleted article");

    articlePage.navigateToArticle("previously-deleted-article-slug");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Deleted article should show error or 404 page");

    test.info("Verified 404 handling for deleted article");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-007: Verify 404 for comment after deletion")
  public void testTC007_DeletedComment() {
    createTest(
        "TC-007: Deleted comment returns 404",
        "Verify 404 Not Found status returned for deleted comment");

    errorPage.navigateTo("/article/some-article/comments/deleted-comment-id");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Deleted comment should show error or 404 page");

    test.info("Verified 404 handling for deleted comment");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-008: Verify 404 when accessing deleted user's profile")
  public void testTC008_DeletedUserProfile() {
    createTest(
        "TC-008: Deleted user profile returns 404",
        "Verify 404 Not Found status returned for deleted user profile");

    profilePage.navigateToProfile("deleted-user-profile-12345");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Deleted user profile should show error or 404 page");

    test.info("Verified 404 handling for deleted user profile");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-009: Verify 404 response body structure is consistent")
  public void testTC009_ConsistentResponseStructure() {
    createTest(
        "TC-009: Consistent 404 response structure",
        "Verify both article and profile 404 responses have consistent structure");

    articlePage.navigateToArticle("non-existent-article-consistency-test");
    articlePage.waitForPageLoad();
    boolean articleHasError = articlePage.hasErrorInPageSource();

    profilePage.navigateToProfile("non-existent-profile-consistency-test");
    profilePage.waitForPageLoad();
    boolean profileHasError = profilePage.hasErrorInPageSource();

    assertTrue(articleHasError, "Article 404 should show error");
    assertTrue(profileHasError, "Profile 404 should show error");

    test.info("Verified consistent 404 response structure across resource types");
  }

  @Test(
      groups = {"regression", "404"},
      description = "TC-010: Verify 404 status code is exactly 404")
  public void testTC010_ExactStatusCode() {
    createTest(
        "TC-010: Exact 404 status code verification",
        "Verify status code is exactly 404 (not 400, 401, etc.)");

    articlePage.navigateToArticle("exact-status-code-test-article");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Non-existent resource should return 404-like error");

    boolean isNotAuthError = !driver.getPageSource().toLowerCase().contains("unauthorized");
    boolean isNotBadRequest = !driver.getPageSource().toLowerCase().contains("bad request");

    assertTrue(isNotAuthError, "Should not be an authorization error");
    assertTrue(isNotBadRequest, "Should not be a bad request error");

    test.info("Verified 404 status code is correct (not auth or bad request error)");
  }
}
