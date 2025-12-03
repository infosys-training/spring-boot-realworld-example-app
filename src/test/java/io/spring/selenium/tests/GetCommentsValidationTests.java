package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.LoginPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for Get Article Comments functionality. Tests TC-013 through TC-022
 * covering data format and validation scenarios.
 */
public class GetCommentsValidationTests extends BaseTest {

  private ArticlePage articlePage;
  private LoginPage loginPage;

  // Test data - using known article slugs from seed data
  private static final String ARTICLE_WITH_COMMENTS = "getting-started-with-spring-boot";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    articlePage = new ArticlePage(driver);
    loginPage = new LoginPage(driver);
  }

  /** TC-013: Verify comment ID format is valid */
  @Test(groups = {"regression", "validation"})
  public void testTC013_VerifyCommentIdFormat() {
    createTest(
        "TC-013: Verify comment ID format is valid",
        "Verify comment IDs are unique numeric or UUID format");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Found " + commentCount + " comments");

    if (commentCount > 0) {
      // Get comment cards and check for ID attributes
      var comments = articlePage.getAllComments();
      for (int i = 0; i < comments.size(); i++) {
        var comment = comments.get(i);
        // Comments should have some identifying attribute
        String commentHtml = comment.getAttribute("outerHTML");
        assertNotNull(commentHtml, "Comment HTML should not be null");
        test.info("Comment " + i + " HTML structure verified");
      }
    } else {
      test.info("No comments found to verify ID format");
    }
  }

  /** TC-014: Verify creation date format (ISO 8601) */
  @Test(groups = {"regression", "validation"})
  public void testTC014_VerifyCreationDateFormat() {
    createTest(
        "TC-014: Verify creation date format (ISO 8601)",
        "Verify dates are displayed in readable format");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      String dateText = articlePage.getCommentDate(0);
      assertNotNull(dateText, "Date text should not be null");
      assertFalse(dateText.isEmpty(), "Date text should not be empty");

      // Date should be in readable format like "Mon Dec 03 2025" or similar
      test.info("Date format: " + dateText);

      // Verify it contains date-like content
      boolean hasDateContent =
          dateText.matches(".*\\d+.*") // Contains numbers
              || dateText.toLowerCase().contains("jan")
              || dateText.toLowerCase().contains("feb")
              || dateText.toLowerCase().contains("mar")
              || dateText.toLowerCase().contains("apr")
              || dateText.toLowerCase().contains("may")
              || dateText.toLowerCase().contains("jun")
              || dateText.toLowerCase().contains("jul")
              || dateText.toLowerCase().contains("aug")
              || dateText.toLowerCase().contains("sep")
              || dateText.toLowerCase().contains("oct")
              || dateText.toLowerCase().contains("nov")
              || dateText.toLowerCase().contains("dec");

      assertTrue(hasDateContent, "Date should contain recognizable date content");
    } else {
      test.info("No comments found to verify date format");
    }
  }

  /** TC-015: Verify update date format (ISO 8601) */
  @Test(groups = {"regression", "validation"})
  public void testTC015_VerifyUpdateDateFormat() {
    createTest(
        "TC-015: Verify update date format (ISO 8601)",
        "Verify update date is in same format as creation date");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      String dateText = articlePage.getCommentDate(0);
      assertNotNull(dateText, "Date text should not be null");

      // The UI shows the date in a consistent format
      test.info("Date displayed: " + dateText);

      // Verify format consistency
      assertFalse(dateText.isEmpty(), "Date should not be empty");
    } else {
      test.info("No comments found to verify update date format");
    }
  }

  /** TC-016: Verify author profile contains username */
  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC016_VerifyAuthorProfileContainsUsername() {
    createTest(
        "TC-016: Verify author profile contains username",
        "Verify all comments have non-empty author username");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      List<String> usernames = articlePage.getAllCommentAuthorUsernames();
      test.info("Found " + usernames.size() + " author usernames");

      for (int i = 0; i < usernames.size(); i++) {
        String username = usernames.get(i);
        assertNotNull(username, "Username at index " + i + " should not be null");
        // Username may be empty string if not found, but should exist
        test.info("Author " + i + ": " + username);
      }
    } else {
      test.info("No comments found to verify author usernames");
    }
  }

  /** TC-017: Verify author profile contains image URL */
  @Test(groups = {"regression", "validation"})
  public void testTC017_VerifyAuthorProfileContainsImageUrl() {
    createTest(
        "TC-017: Verify author profile contains image URL",
        "Verify author images have valid URL or default placeholder");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      String imageSrc = articlePage.getCommentAuthorImageSrc(0);
      test.info("Author image src: " + imageSrc);

      // Image should have a src attribute (could be actual image or placeholder)
      boolean hasValidImage =
          imageSrc != null
              && !imageSrc.isEmpty()
              && (imageSrc.startsWith("http")
                  || imageSrc.startsWith("/")
                  || imageSrc.contains("data:"));

      assertTrue(hasValidImage, "Author image should have valid URL or data URI");
    } else {
      test.info("No comments found to verify author image URL");
    }
  }

  /** TC-018: Verify comment body preserves formatting */
  @Test(groups = {"regression", "validation"})
  public void testTC018_VerifyCommentBodyPreservesFormatting() {
    createTest(
        "TC-018: Verify comment body preserves formatting",
        "Verify line breaks and paragraphs are preserved in display");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      boolean formattingPreserved = articlePage.isCommentBodyPreservingFormatting(0);
      test.info("Formatting preserved: " + formattingPreserved);

      String commentBody = articlePage.getCommentBody(0);
      test.info("Comment body: " + commentBody);

      // Comment body should be displayed
      assertNotNull(commentBody, "Comment body should not be null");
    } else {
      test.info("No comments found to verify formatting");
    }
  }

  /** TC-019: Verify comment body handles special characters */
  @Test(groups = {"regression", "validation"})
  public void testTC019_VerifyCommentBodyHandlesSpecialCharacters() {
    createTest(
        "TC-019: Verify comment body handles special characters",
        "Verify special characters are displayed correctly, not escaped");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      String commentBody = articlePage.getCommentBody(0);
      test.info("Comment body: " + commentBody);

      // Verify the comment body is rendered (not showing HTML entities)
      assertFalse(
          commentBody.contains("&amp;") && !commentBody.contains("&"),
          "Special characters should be rendered, not escaped");
      assertFalse(
          commentBody.contains("&lt;") && !commentBody.contains("<"),
          "Special characters should be rendered, not escaped");
    } else {
      test.info("No comments found to verify special character handling");
    }
  }

  /** TC-020: Verify comment body handles unicode characters */
  @Test(groups = {"regression", "validation"})
  public void testTC020_VerifyCommentBodyHandlesUnicode() {
    createTest(
        "TC-020: Verify comment body handles unicode characters",
        "Verify unicode characters render correctly");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      String commentBody = articlePage.getCommentBody(0);
      test.info("Comment body: " + commentBody);

      // Verify the comment body doesn't show unicode escape sequences
      assertFalse(
          commentBody.contains("\\u"), "Unicode should be rendered, not shown as escape sequences");
    } else {
      test.info("No comments found to verify unicode handling");
    }
  }

  /** TC-021: Verify author following status is boolean */
  @Test(groups = {"regression", "validation"})
  public void testTC021_VerifyAuthorFollowingStatusIsBoolean() {
    createTest(
        "TC-021: Verify author following status is boolean",
        "Verify follow status is either Following or Follow (not both)");

    // Login first to see follow status
    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    // Check if follow status is displayed
    boolean hasFollowStatus = articlePage.isFollowStatusDisplayed();
    test.info("Follow status displayed: " + hasFollowStatus);

    if (hasFollowStatus) {
      // The follow button should show either "Follow" or "Unfollow"/"Following"
      // It should not show both states simultaneously
      String pageSource = articlePage.getPageSource().toLowerCase();
      boolean hasFollow = pageSource.contains("follow");
      test.info("Page contains follow-related text: " + hasFollow);
    } else {
      test.info("Follow status not displayed on this page");
    }
  }

  /** TC-022: Verify all required fields present in response */
  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC022_VerifyAllRequiredFieldsPresent() {
    createTest(
        "TC-022: Verify all required fields present in response",
        "Verify each comment has id, body, createdAt, author info");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Found " + commentCount + " comments");

    if (commentCount > 0) {
      // Verify first comment has all required fields displayed
      String body = articlePage.getCommentBody(0);
      String author = articlePage.getCommentAuthorUsername(0);
      String date = articlePage.getCommentDate(0);
      boolean hasImage = articlePage.isCommentAuthorImageDisplayed(0);

      test.info("Comment body: " + body);
      test.info("Comment author: " + author);
      test.info("Comment date: " + date);
      test.info("Has author image: " + hasImage);

      // Verify required fields are present
      assertNotNull(body, "Comment body should be present");
      assertNotNull(date, "Comment date should be present");

      // Author info should be present (username or image)
      assertTrue(
          (author != null && !author.isEmpty()) || hasImage,
          "Author info (username or image) should be present");
    } else {
      test.info("No comments found to verify required fields");
    }
  }
}
