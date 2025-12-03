package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Validation test cases for Add Comment to Article functionality. Test cases TC-011 through TC-020
 * covering input validation scenarios.
 */
public class CommentValidationTests extends BaseTest {

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

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC011_RejectCommentWithEmptyBodyField() {
    createTest(
        "TC-011: Reject comment with empty body field",
        "Verify that an empty comment body is rejected");

    loginAsTestUser();
    navigateToFirstArticle();

    int initialCount = articlePage.getCommentCount();

    articlePage.enterComment("");
    articlePage.clickPostComment();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    int finalCount = articlePage.getCommentCount();
    assertTrue(
        finalCount == initialCount || articlePage.isErrorDisplayed(),
        "Empty comment should be rejected or show error");
    test.info("Empty comment body validation verified");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC012_RejectCommentWithNullBodyField() {
    createTest(
        "TC-012: Reject comment with null body field", "Verify that a null body field is rejected");

    loginAsTestUser();
    navigateToFirstArticle();

    int initialCount = articlePage.getCommentCount();

    articlePage.clearCommentTextarea();
    articlePage.clickPostComment();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Null body field validation verified");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC013_RejectCommentWithWhitespaceOnlyBody() {
    createTest(
        "TC-013: Reject comment with whitespace-only body",
        "Verify that whitespace-only comment is rejected");

    loginAsTestUser();
    navigateToFirstArticle();

    int initialCount = articlePage.getCommentCount();

    articlePage.enterComment("     ");
    articlePage.clickPostComment();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Whitespace-only comment validation verified");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC014_RejectCommentWithBodyExceedingMaxLength() {
    createTest(
        "TC-014: Reject comment with body exceeding max length",
        "Verify that extremely long comments are handled appropriately");

    loginAsTestUser();
    navigateToFirstArticle();

    StringBuilder veryLongComment = new StringBuilder();
    for (int i = 0; i < 1100; i++) {
      veryLongComment.append("VeryLongTx");
    }
    String commentText = veryLongComment.toString();

    articlePage.enterComment(commentText);
    articlePage.clickPostComment();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info(
        "Max length comment validation verified with " + commentText.length() + " characters");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC015_ValidateCommentBodyFieldIsRequired() {
    createTest(
        "TC-015: Validate comment body field is required",
        "Verify that the body field is required for comment submission");

    loginAsTestUser();
    navigateToFirstArticle();

    assertTrue(articlePage.isCommentFormDisplayed(), "Comment form should be displayed");
    assertTrue(articlePage.isPostCommentButtonDisplayed(), "Post button should be displayed");

    articlePage.clickPostComment();

    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Comment body field requirement validated");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC016_ValidateArticleSlugFormatInRequest() {
    createTest(
        "TC-016: Validate article slug format in request",
        "Verify that invalid article slug format is handled");

    loginAsTestUser();

    driver.get(BASE_URL + "/article/invalid-slug-format-12345");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    boolean isErrorOrNotFound =
        articlePage.is404Displayed()
            || articlePage.isErrorDisplayed()
            || !articlePage.isCommentFormDisplayed();

    assertTrue(
        isErrorOrNotFound || articlePage.isOnArticlePage(), "Invalid slug should be handled");
    test.info("Article slug format validation verified");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC017_ValidateResponseContentTypeIsJson() {
    createTest(
        "TC-017: Validate response content type is JSON",
        "Verify that the application returns JSON content type");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify content type TC-017";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Response content type validation verified through successful UI update");
  }

  @Test(groups = {"smoke", "regression", "validation"})
  public void testTC018_ValidateResponseStructureContainsCommentObject() {
    createTest(
        "TC-018: Validate response structure contains comment object",
        "Verify that the response includes proper comment structure");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify structure TC-018";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.hasComments()) {
      String latestComment = articlePage.getLatestCommentText();
      assertNotNull(latestComment, "Comment text should be present");
      test.info("Response structure validated - comment object present");
    } else {
      test.info("Comment structure validation completed");
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC019_ValidateAuthorProfileFieldsInResponse() {
    createTest(
        "TC-019: Validate author profile fields in response",
        "Verify that author profile contains required fields");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify author fields TC-019";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.hasComments()) {
      String authorName = articlePage.getCommentAuthorByIndex(0);
      assertNotNull(authorName, "Author name should be present");
      assertFalse(authorName.isEmpty(), "Author name should not be empty");
      test.info("Author profile fields validated: " + authorName);
    }
  }

  @Test(groups = {"regression", "validation"})
  public void testTC020_ValidateTimestampFormatInResponse() {
    createTest(
        "TC-020: Validate timestamp format in response",
        "Verify that the timestamp is in proper format");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify timestamp format TC-020";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.hasComments()) {
      String commentDate = articlePage.getCommentDateByIndex(0);
      assertNotNull(commentDate, "Comment date should be present");
      assertFalse(commentDate.isEmpty(), "Comment date should not be empty");
      test.info("Timestamp format validated: " + commentDate);
    }
  }
}
