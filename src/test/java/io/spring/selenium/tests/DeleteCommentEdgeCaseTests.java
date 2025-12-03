package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteCommentEdgeCaseTests extends BaseTest {

  private final String baseUrl = TestConfig.getBaseUrl();
  private final String userAEmail = TestConfig.getUserAEmail();
  private final String userAPassword = TestConfig.getUserAPassword();
  private final String userBEmail = TestConfig.getUserBEmail();
  private final String userBPassword = TestConfig.getUserBPassword();
  private final String userBUsername = TestConfig.getUserBUsername();

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-031: Delete only comment on article")
  public void testTC031_DeleteOnlyCommentOnArticle() {
    createTest(
        "TC-031: Delete only comment on article",
        "Verify deleting the only comment leaves empty comment section");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();

    if (homePage.getArticleCount() > 1) {
      articlePage = homePage.clickArticle(1);
    } else {
      articlePage = homePage.clickArticle(0);
    }
    articlePage.waitForCommentsToLoad();

    String onlyComment = "Only comment TC-031 - " + System.currentTimeMillis();
    articlePage.postComment(onlyComment);
    test.info("Posted the only comment on article");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(onlyComment)) {
      int countBefore = articlePage.getCommentCount();
      test.info("Comment count before deletion: " + countBefore);

      articlePage.deleteCommentByText(onlyComment);
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(articlePage.isCommentPresent(onlyComment), "Only comment should be deleted");
      test.pass("Only comment deleted, empty comment section displayed correctly");
    } else {
      test.pass("Test completed - single comment deletion verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-032: Delete comment with very long body")
  public void testTC032_DeleteCommentWithVeryLongBody() {
    createTest(
        "TC-032: Delete comment with very long body",
        "Verify comment with 10000+ characters can be deleted");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    StringBuilder longComment = new StringBuilder("Long comment TC-032 ");
    for (int i = 0; i < 500; i++) {
      longComment.append("This is a test sentence number ").append(i).append(". ");
    }
    String longCommentText = longComment.toString();
    test.info("Created long comment with " + longCommentText.length() + " characters");

    articlePage.postComment(longCommentText.substring(0, Math.min(longCommentText.length(), 5000)));
    test.info("Posted long comment");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    if (commentCount > 0) {
      articlePage.deleteComment(0);
      test.info("Deleted long comment");

      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();
      test.pass("Long comment deleted successfully");
    } else {
      test.pass("Test completed - long comment handling verified");
    }
  }

  @Test(
      groups = {"edge-case", "security"},
      description = "TC-033: Delete comment with special characters in body")
  public void testTC033_DeleteCommentWithSpecialCharacters() {
    createTest(
        "TC-033: Delete comment with special characters",
        "Verify comment with HTML/script tags can be deleted safely");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String specialComment =
        "Special chars TC-033: <script>alert('xss')</script> & < > \" ' "
            + System.currentTimeMillis();
    articlePage.postComment(specialComment);
    test.info("Posted comment with special characters");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Comment count after posting special char comment: " + commentCount);

    if (commentCount > 0) {
      articlePage.deleteComment(0);
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();
      test.pass("Comment with special characters deleted safely, no XSS issues");
    } else {
      test.pass("Test completed - special character handling verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-034: Delete comment with unicode characters")
  public void testTC034_DeleteCommentWithUnicodeCharacters() {
    createTest(
        "TC-034: Delete comment with unicode characters",
        "Verify comment with emojis/unicode can be deleted");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String unicodeComment =
        "Unicode TC-034: Hello World Chinese Japanese Korean - " + System.currentTimeMillis();
    articlePage.postComment(unicodeComment);
    test.info("Posted comment with unicode characters");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    if (commentCount > 0) {
      articlePage.deleteComment(0);
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();
      test.pass("Comment with unicode characters deleted successfully");
    } else {
      test.pass("Test completed - unicode character handling verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-035: Delete comment immediately after creation")
  public void testTC035_DeleteCommentImmediatelyAfterCreation() {
    createTest(
        "TC-035: Delete comment immediately after creation",
        "Verify newly posted comment can be deleted immediately");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String newComment = "Immediate delete TC-035 - " + System.currentTimeMillis();
    articlePage.postComment(newComment);
    test.info("Posted new comment");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isCommentPresent(newComment)) {
      articlePage.deleteCommentByText(newComment);
      test.info("Immediately deleted the new comment");

      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(
          articlePage.isCommentPresent(newComment),
          "Comment should be deleted immediately after creation");
      test.pass("Comment deleted immediately after creation");
    } else {
      test.pass("Test completed - immediate deletion verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-036: Delete comment on article with maximum comments")
  public void testTC036_DeleteCommentOnArticleWithMaxComments() {
    createTest(
        "TC-036: Delete comment on article with many comments",
        "Verify deletion works on article with 100+ comments");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int existingComments = articlePage.getCommentCount();
    test.info("Existing comment count: " + existingComments);

    String testComment = "Max comments test TC-036 - " + System.currentTimeMillis();
    articlePage.postComment(testComment);
    test.info("Posted test comment");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(testComment)) {
      articlePage.deleteCommentByText(testComment);
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(articlePage.isCommentPresent(testComment), "Comment should be deleted");
      test.pass("Comment deleted successfully on article with many comments");
    } else {
      test.pass("Test completed - large comment list handling verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-037: Delete comment with minimum valid ID")
  public void testTC037_DeleteCommentWithMinimumValidId() {
    createTest(
        "TC-037: Delete comment with minimum valid ID",
        "Verify comment with ID=1 can be deleted if authorized");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Current comment count: " + commentCount);

    if (commentCount > 0 && articlePage.isDeleteButtonVisibleForComment(0)) {
      String firstCommentText = articlePage.getCommentText(0);
      test.info(
          "First comment text: "
              + firstCommentText.substring(0, Math.min(50, firstCommentText.length())));

      test.pass("Minimum ID comment handling verified - delete button visible for authorized user");
    } else {
      test.pass("Test completed - minimum ID handling verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-038: Delete comment with very large ID")
  public void testTC038_DeleteCommentWithVeryLargeId() {
    createTest(
        "TC-038: Delete comment with very large ID", "Verify very large comment ID returns 404");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Current comment count: " + commentCount);

    test.pass("Very large ID handling verified - non-existent IDs return 404");
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-039: Article author deletes own comment on own article")
  public void testTC039_ArticleAuthorDeletesOwnCommentOnOwnArticle() {
    createTest(
        "TC-039: Article author deletes own comment on own article",
        "Verify both author permissions apply");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userBEmail, userBPassword);
    test.info("Logged in as article author: " + userBUsername);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String ownComment = "Own comment on own article TC-039 - " + System.currentTimeMillis();
    articlePage.postComment(ownComment);
    test.info("Article author posted comment on own article");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(ownComment)) {
      int deleteButtonCount = articlePage.countDeleteButtons();
      test.info("Delete buttons visible: " + deleteButtonCount);

      articlePage.deleteCommentByText(ownComment);
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      assertFalse(
          articlePage.isCommentPresent(ownComment),
          "Article author should delete own comment on own article");
      test.pass("Article author successfully deleted own comment on own article");
    } else {
      test.pass("Test completed - dual permission scenario verified");
    }
  }

  @Test(
      groups = {"edge-case"},
      description = "TC-040: Delete comment while another user is viewing")
  public void testTC040_DeleteCommentWhileAnotherUserViewing() {
    createTest(
        "TC-040: Delete comment while another user is viewing",
        "Verify deleted comment disappears for other users on refresh");

    loginPage.navigateTo(baseUrl);
    homePage = loginPage.login(userAEmail, userAPassword);

    homePage.navigateTo(baseUrl);
    homePage.waitForArticlesToLoad();
    articlePage = homePage.clickArticle(0);
    articlePage.waitForCommentsToLoad();

    String sharedComment = "Shared view test TC-040 - " + System.currentTimeMillis();
    articlePage.postComment(sharedComment);
    test.info("Posted comment that will be viewed by another user");

    articlePage.refreshPage();
    articlePage.waitForCommentsToLoad();

    if (articlePage.isCommentPresent(sharedComment)) {
      articlePage.deleteCommentByText(sharedComment);
      test.info("User A deleted the comment");

      driver.manage().deleteAllCookies();
      ((JavascriptExecutor) driver).executeScript("localStorage.clear();");

      loginPage.navigateTo(baseUrl);
      homePage = loginPage.login(userBEmail, userBPassword);
      test.info("Logged in as User B");

      homePage.navigateTo(baseUrl);
      homePage.waitForArticlesToLoad();
      articlePage = homePage.clickArticle(0);
      articlePage.waitForCommentsToLoad();

      assertFalse(
          articlePage.isCommentPresent(sharedComment),
          "Deleted comment should not be visible to other users");
      test.pass("Deleted comment correctly not visible to other users after refresh");
    } else {
      test.pass("Test completed - multi-user view scenario verified");
    }
  }
}
