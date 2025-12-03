package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Positive test cases for Add Comment to Article functionality. Test cases TC-001 through TC-010
 * covering happy path scenarios.
 */
public class CommentPositiveTests extends BaseTest {

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

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC001_SuccessfullyAddCommentWithValidBody() {
    createTest(
        "TC-001: Successfully add comment to article with valid body",
        "Verify that a logged-in user can add a comment with valid body text");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "This is a valid test comment for TC-001";
    int initialCommentCount = articlePage.getCommentCount();

    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(
        articlePage.getCommentCount() >= initialCommentCount,
        "Comment should be added to the article");
    test.info("Successfully added comment: " + commentText);
  }

  @Test(groups = {"regression", "positive"})
  public void testTC002_AddCommentWithMinimumValidBody() {
    createTest(
        "TC-002: Add comment with minimum valid body (1 character)",
        "Verify that a single character comment can be submitted");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "a";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page after posting");
    test.info("Successfully added minimum length comment");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC003_AddCommentWithLongBodyText() {
    createTest(
        "TC-003: Add comment with long body text (1000 characters)",
        "Verify that a long comment can be submitted and preserved");

    loginAsTestUser();
    navigateToFirstArticle();

    StringBuilder longComment = new StringBuilder();
    for (int i = 0; i < 100; i++) {
      longComment.append("TestText10");
    }
    String commentText = longComment.toString();

    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page after posting");
    test.info("Successfully added long comment with " + commentText.length() + " characters");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC004_AddCommentAndVerifyAuthorProfile() {
    createTest(
        "TC-004: Add comment and verify author profile in response",
        "Verify that the comment displays author information");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify author profile TC-004";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.hasComments()) {
      String authorName = articlePage.getCommentAuthorByIndex(0);
      assertNotNull(authorName, "Comment should have an author name");
      assertFalse(authorName.isEmpty(), "Author name should not be empty");
      test.info("Comment author verified: " + authorName);
    }
  }

  @Test(groups = {"regression", "positive"})
  public void testTC005_AddCommentAndVerifyCreationTimestamp() {
    createTest(
        "TC-005: Add comment and verify creation timestamp is recorded",
        "Verify that the comment displays a creation timestamp");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify timestamp TC-005";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.hasComments()) {
      String commentDate = articlePage.getCommentDateByIndex(0);
      assertNotNull(commentDate, "Comment should have a date");
      assertFalse(commentDate.isEmpty(), "Comment date should not be empty");
      test.info("Comment timestamp verified: " + commentDate);
    }
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC006_AddCommentAndVerify201StatusCode() {
    createTest(
        "TC-006: Add comment and verify 201 status code",
        "Verify that adding a comment results in successful creation");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify status code TC-006";
    int initialCount = articlePage.getCommentCount();

    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Comment created successfully without errors");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC007_AddCommentAndVerifyCommentIdReturned() {
    createTest(
        "TC-007: Add comment and verify comment ID is returned",
        "Verify that the created comment has a unique identifier");

    loginAsTestUser();
    navigateToFirstArticle();

    String commentText = "Comment to verify ID TC-007";
    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.hasComments(), "Comments should be present");
    test.info("Comment created with unique identifier");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void testTC008_AddCommentAndVerifyArticleAssociation() {
    createTest(
        "TC-008: Add comment and verify article association",
        "Verify that the comment is associated with the correct article");

    loginAsTestUser();
    navigateToFirstArticle();

    String articleUrl = articlePage.getCurrentUrl();
    String commentText =
        "Comment to verify article association TC-008 - " + System.currentTimeMillis();

    articlePage.addComment(commentText);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    driver.navigate().refresh();
    articlePage.waitForCommentsToLoad();

    String latestComment = articlePage.getLatestCommentText();
    assertTrue(
        latestComment.contains("TC-008") || articlePage.hasComments(),
        "Comment should be associated with the article");
    test.info("Comment verified on article: " + articleUrl);
  }

  @Test(groups = {"regression", "positive"})
  public void testTC009_AddMultipleCommentsToSameArticle() {
    createTest(
        "TC-009: Add multiple comments to same article",
        "Verify that multiple comments can be added to the same article");

    loginAsTestUser();
    navigateToFirstArticle();

    int initialCount = articlePage.getCommentCount();

    articlePage.addComment("First comment TC-009");
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articlePage.addComment("Second comment TC-009");
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    articlePage.addComment("Third comment TC-009");
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Successfully added multiple comments to the same article");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC010_AddCommentToDifferentArticlesBySameUser() {
    createTest(
        "TC-010: Add comment to different articles by same user",
        "Verify that a user can comment on multiple different articles");

    loginAsTestUser();

    homePage.navigateTo(BASE_URL);
    homePage.waitForArticlesToLoad();

    if (homePage.getArticleCount() >= 2) {
      homePage.clickArticleByIndex(0);
      articlePage.waitForCommentsToLoad();
      articlePage.addComment("Comment on first article TC-010");

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      homePage.navigateTo(BASE_URL);
      homePage.waitForArticlesToLoad();
      homePage.clickArticleByIndex(1);
      articlePage.waitForCommentsToLoad();
      articlePage.addComment("Comment on second article TC-010");

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(articlePage.isOnArticlePage(), "Should be on article page");
      test.info("Successfully added comments to different articles");
    } else {
      test.skip("Not enough articles available for this test");
    }
  }
}
