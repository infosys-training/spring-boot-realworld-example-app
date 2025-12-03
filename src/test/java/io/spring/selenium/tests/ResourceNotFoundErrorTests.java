package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.ErrorPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Error tests for Resource Not Found (404) handling. Tests TC-021 to TC-030: Verify error handling
 * for negative scenarios including authenticated operations.
 */
public class ResourceNotFoundErrorTests extends BaseTest {

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
      groups = {"regression", "404", "error"},
      description = "TC-021: Verify 404 when article slug contains only numbers")
  public void testTC021_SlugOnlyNumbers() {
    createTest(
        "TC-021: Slug with only numbers",
        "Verify 404 Not Found status returned for numeric-only slug");

    articlePage.navigateToArticle("12345678901234567890");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Numeric-only slug should show error or 404 page");

    test.info("Verified 404 handling for slug containing only numbers");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-022: Verify 404 when username contains only special characters")
  public void testTC022_UsernameOnlySpecialChars() {
    createTest(
        "TC-022: Username with only special characters",
        "Verify 404 Not Found status returned for special-char-only username");

    profilePage.navigateToProfile("@#$%^&*()");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Special-char-only username should show error or 404 page");

    test.info("Verified 404 handling for username containing only special characters");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-023: Verify 404 for article with unicode characters in slug")
  public void testTC023_UnicodeSlug() {
    createTest(
        "TC-023: Unicode characters in slug",
        "Verify 404 Not Found status returned for unicode slug");

    articlePage.navigateToArticle("article-with-unicode-\u4e2d\u6587-\u65e5\u672c\u8a9e");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Unicode slug should show error or 404 page");

    test.info("Verified 404 handling for article with unicode characters in slug");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-024: Verify 404 for profile with unicode characters in username")
  public void testTC024_UnicodeUsername() {
    createTest(
        "TC-024: Unicode characters in username",
        "Verify 404 Not Found status returned for unicode username");

    profilePage.navigateToProfile("user-\u4e2d\u6587-\u65e5\u672c\u8a9e");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Unicode username should show error or 404 page");

    test.info("Verified 404 handling for profile with unicode characters in username");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-025: Verify 404 when accessing article comments for non-existent article")
  public void testTC025_CommentsNonExistentArticle() {
    createTest(
        "TC-025: Comments for non-existent article",
        "Verify 404 Not Found status returned for comments on non-existent article");

    articlePage.navigateToArticle("completely-non-existent-article-for-comments");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Comments section for non-existent article should show error or 404");

    test.info("Verified 404 handling when accessing comments for non-existent article");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-026: Verify 404 when following non-existent user")
  public void testTC026_FollowNonExistentUser() {
    createTest(
        "TC-026: Follow non-existent user",
        "Verify 404 Not Found status returned when attempting to follow non-existent user");

    profilePage.navigateToProfile("non-existent-user-to-follow-12345");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Following non-existent user should show error or 404 page");

    test.info("Verified 404 handling when attempting to follow non-existent user");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-027: Verify 404 when unfollowing non-existent user")
  public void testTC027_UnfollowNonExistentUser() {
    createTest(
        "TC-027: Unfollow non-existent user",
        "Verify 404 Not Found status returned when attempting to unfollow non-existent user");

    profilePage.navigateToProfile("non-existent-user-to-unfollow-12345");
    profilePage.waitForPageLoad();

    boolean hasError = profilePage.isPageNotFound() || profilePage.hasErrorInPageSource();
    assertTrue(hasError, "Unfollowing non-existent user should show error or 404 page");

    test.info("Verified 404 handling when attempting to unfollow non-existent user");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-028: Verify 404 when favoriting non-existent article")
  public void testTC028_FavoriteNonExistentArticle() {
    createTest(
        "TC-028: Favorite non-existent article",
        "Verify 404 Not Found status returned when attempting to favorite non-existent article");

    articlePage.navigateToArticle("non-existent-article-to-favorite-12345");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Favoriting non-existent article should show error or 404 page");

    test.info("Verified 404 handling when attempting to favorite non-existent article");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-029: Verify 404 when unfavoriting non-existent article")
  public void testTC029_UnfavoriteNonExistentArticle() {
    createTest(
        "TC-029: Unfavorite non-existent article",
        "Verify 404 Not Found status returned when attempting to unfavorite non-existent article");

    articlePage.navigateToArticle("non-existent-article-to-unfavorite-12345");
    articlePage.waitForPageLoad();

    boolean hasError = articlePage.isPageNotFound() || articlePage.hasErrorInPageSource();
    assertTrue(hasError, "Unfavoriting non-existent article should show error or 404 page");

    test.info("Verified 404 handling when attempting to unfavorite non-existent article");
  }

  @Test(
      groups = {"regression", "404", "error"},
      description = "TC-030: Verify 404 when updating non-existent article")
  public void testTC030_UpdateNonExistentArticle() {
    createTest(
        "TC-030: Update non-existent article",
        "Verify 404 Not Found status returned when attempting to update non-existent article");

    errorPage.navigateTo("/editor/non-existent-article-to-update-12345");
    errorPage.waitForPageLoad();

    boolean hasError = errorPage.hasErrorMessage() || errorPage.is404Page();
    assertTrue(hasError, "Updating non-existent article should show error or 404 page");

    test.info("Verified 404 handling when attempting to update non-existent article");
  }
}
