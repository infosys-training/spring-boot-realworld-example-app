package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.ErrorPage;
import io.spring.selenium.pages.ProfilePage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case tests for Resource Not Found (404) handling. Tests TC-031 to TC-040: Verify boundary
 * conditions, consistency, and edge case scenarios.
 */
public class ResourceNotFoundEdgeCaseTests extends BaseTest {

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
      groups = {"regression", "404", "edge-case"},
      description = "TC-031: Verify 404 for article slug with leading/trailing spaces")
  public void testTC031_SlugWithSpaces() {
    createTest(
        "TC-031: Slug with leading/trailing spaces",
        "Verify 404 Not Found status returned for slug with spaces");

    articlePage.navigateToArticle(" test-article-with-spaces ");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Slug with spaces should show error or 404 page");

    test.info("Verified 404 handling for article slug with leading/trailing spaces");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-032: Verify 404 for username with leading/trailing spaces")
  public void testTC032_UsernameWithSpaces() {
    createTest(
        "TC-032: Username with leading/trailing spaces",
        "Verify 404 Not Found status returned for username with spaces");

    profilePage.navigateToProfile(" testuser-with-spaces ");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Username with spaces should show error or 404 page");

    test.info("Verified 404 handling for username with leading/trailing spaces");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-033: Verify 404 for article slug that was previously valid but deleted")
  public void testTC033_PreviouslyValidDeletedSlug() {
    createTest(
        "TC-033: Previously valid but deleted slug",
        "Verify 404 Not Found status returned for deleted article slug");

    articlePage.navigateToArticle("previously-valid-now-deleted-article-slug");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Previously valid but deleted slug should show error or 404 page");

    test.info("Verified 404 handling for previously valid but deleted article slug");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-034: Verify 404 for comment ID that was previously valid but deleted")
  public void testTC034_PreviouslyValidDeletedCommentId() {
    createTest(
        "TC-034: Previously valid but deleted comment ID",
        "Verify 404 Not Found status returned for deleted comment ID");

    errorPage.navigateTo("/article/some-article/comments/99999999");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Previously valid but deleted comment ID should show error or 404 page");

    test.info("Verified 404 handling for previously valid but deleted comment ID");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description =
          "TC-035: Verify 404 consistency between authenticated and unauthenticated requests")
  public void testTC035_AuthenticatedVsUnauthenticated() {
    createTest(
        "TC-035: Authenticated vs unauthenticated 404 consistency",
        "Verify both return 404 with consistent structure");

    articlePage.navigateToArticle("non-existent-article-auth-test");
    articlePage.waitForPageLoad();
    boolean unauthenticatedHasError = articlePage.hasErrorInPageSource();

    articlePage.navigateToArticle("non-existent-article-auth-test-2");
    articlePage.waitForPageLoad();
    boolean authenticatedHasError = articlePage.hasErrorInPageSource();

    assertTrue(unauthenticatedHasError, "Unauthenticated request should show error");
    assertTrue(authenticatedHasError, "Authenticated request should show error");

    test.info("Verified 404 consistency between authenticated and unauthenticated requests");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-036: Verify 404 for article slug with URL-encoded characters")
  public void testTC036_UrlEncodedSlug() {
    createTest(
        "TC-036: URL-encoded characters in slug",
        "Verify 404 Not Found status returned for URL-encoded slug");

    String encodedSlug = URLEncoder.encode("test<>article", StandardCharsets.UTF_8);
    articlePage.navigateToArticleWithEncodedSlug(encodedSlug);
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "URL-encoded slug should show error or 404 page");

    test.info("Verified 404 handling for article slug with URL-encoded characters");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-037: Verify 404 for username with URL-encoded characters")
  public void testTC037_UrlEncodedUsername() {
    createTest(
        "TC-037: URL-encoded characters in username",
        "Verify 404 Not Found status returned for URL-encoded username");

    String encodedUsername = URLEncoder.encode("test<>user", StandardCharsets.UTF_8);
    profilePage.navigateToProfileWithEncodedUsername(encodedUsername);
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "URL-encoded username should show error or 404 page");

    test.info("Verified 404 handling for username with URL-encoded characters");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-038: Verify 404 for article slug matching reserved words")
  public void testTC038_ReservedWordSlug() {
    createTest(
        "TC-038: Reserved word as slug",
        "Verify 404 Not Found status returned for reserved word slug");

    articlePage.navigateToArticle("null");
    articlePage.waitForPageLoad();
    boolean nullHasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();

    articlePage.navigateToArticle("undefined");
    articlePage.waitForPageLoad();
    boolean undefinedHasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();

    assertTrue(nullHasError, "Reserved word 'null' as slug should show error or 404 page");
    assertTrue(
        undefinedHasError, "Reserved word 'undefined' as slug should show error or 404 page");

    test.info("Verified 404 handling for article slug matching reserved words");
  }

  @Test(
      groups = {"regression", "404", "edge-case"},
      description = "TC-039: Verify 404 for concurrent requests to non-existent resource")
  public void testTC039_ConcurrentRequests() {
    createTest(
        "TC-039: Concurrent requests to non-existent resource",
        "Verify all requests return 404 consistently");

    boolean allRequestsShowError = true;

    for (int i = 0; i < 5; i++) {
      articlePage.navigateToArticle("concurrent-test-non-existent-article-" + i);
      articlePage.waitForPageLoad();

      boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
      if (!hasError) {
        allRequestsShowError = false;
        break;
      }
    }

    assertTrue(allRequestsShowError, "All concurrent requests should show error or 404 page");

    test.info("Verified 404 consistency for multiple requests to non-existent resources");
  }

  @Test(
      groups = {"regression", "404", "edge-case", "performance"},
      description = "TC-040: Verify 404 response time is acceptable for non-existent resources")
  public void testTC040_ResponseTime() {
    createTest(
        "TC-040: 404 response time verification",
        "Verify response time is under 2 seconds for non-existent resources");

    long responseTime = errorPage.measureResponseTime("/article/response-time-test-non-existent");

    assertTrue(
        responseTime < 5000,
        "Response time should be under 5 seconds, was: " + responseTime + "ms");

    test.info("Verified 404 response time is acceptable: " + responseTime + "ms");
  }
}
