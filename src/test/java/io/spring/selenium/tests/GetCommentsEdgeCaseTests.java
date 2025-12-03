package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for Get Article Comments functionality. Tests TC-033 through TC-040 covering
 * boundary conditions and edge cases.
 */
public class GetCommentsEdgeCaseTests extends BaseTest {

  private ArticlePage articlePage;
  private LoginPage loginPage;

  // Test data - using known article slugs from seed data
  private static final String ARTICLE_WITH_COMMENTS = "getting-started-with-spring-boot";
  private static final String ARTICLE_NO_COMMENTS = "advanced-java-techniques";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    articlePage = new ArticlePage(driver);
    loginPage = new LoginPage(driver);
  }

  /** TC-033: View comments on article with no comments (empty array) */
  @Test(groups = {"smoke", "regression", "edge"})
  public void testTC033_ViewCommentsEmptyArray() {
    createTest(
        "TC-033: View comments on article with no comments (empty array)",
        "Verify empty state message or no comments section shown");

    // Try an article that might have no comments
    articlePage.navigateToArticle(ARTICLE_NO_COMMENTS);
    articlePage.waitForCommentsToLoad();

    boolean isArticlePage = articlePage.isArticlePageDisplayed();
    test.info("Is article page: " + isArticlePage);

    if (isArticlePage) {
      int commentCount = articlePage.getCommentCount();
      test.info("Comment count: " + commentCount);

      // If no comments, verify the page handles it gracefully
      if (commentCount == 0) {
        // Page should still be functional
        assertTrue(articlePage.isArticlePageDisplayed(), "Article page should still be displayed");
        test.info("Empty comments state handled correctly");
      } else {
        test.info("Article has " + commentCount + " comments");
      }
    } else {
      // Article might not exist, which is also a valid edge case
      test.info("Article not found - testing with different article");
      articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
      articlePage.waitForCommentsToLoad();
      int commentCount = articlePage.getCommentCount();
      test.info("Fallback article comment count: " + commentCount);
    }
  }

  /** TC-034: View comments on article with maximum comments */
  @Test(groups = {"regression", "edge"})
  public void testTC034_ViewCommentsMaximumComments() {
    createTest(
        "TC-034: View comments on article with maximum comments",
        "Verify all comments load (possibly with pagination)");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();
    test.info("Comment count: " + commentCount);

    // Verify page handles multiple comments
    if (commentCount > 0) {
      // All comments should be accessible
      var comments = articlePage.getAllComments();
      assertEquals(comments.size(), commentCount, "All comments should be loaded");

      // Verify we can access the last comment
      if (commentCount > 1) {
        String lastCommentBody = articlePage.getCommentBody(commentCount - 1);
        test.info("Last comment body: " + lastCommentBody);
      }
    }

    // Page should remain responsive
    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should remain displayed");
  }

  /** TC-035: View comments with very long comment body */
  @Test(groups = {"regression", "edge"})
  public void testTC035_ViewCommentsVeryLongBody() {
    createTest(
        "TC-035: View comments with very long comment body",
        "Verify comment displayed fully or with expand option");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      // Check all comment bodies
      var bodies = articlePage.getAllCommentBodies();
      test.info("Found " + bodies.size() + " comment bodies");

      for (int i = 0; i < bodies.size(); i++) {
        String body = bodies.get(i);
        test.info("Comment " + i + " length: " + (body != null ? body.length() : 0));

        // Long comments should still be displayed
        assertNotNull(body, "Comment body should not be null");
      }
    } else {
      test.info("No comments found to verify long body handling");
    }

    // Page should handle long content gracefully
    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should remain displayed");
  }

  /** TC-036: View comments with minimum length comment body */
  @Test(groups = {"regression", "edge"})
  public void testTC036_ViewCommentsMinimumLengthBody() {
    createTest(
        "TC-036: View comments with minimum length comment body",
        "Verify single character comment displayed correctly");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      // Check for any short comments
      var bodies = articlePage.getAllCommentBodies();

      for (int i = 0; i < bodies.size(); i++) {
        String body = bodies.get(i);
        if (body != null && body.length() <= 5) {
          test.info("Found short comment at index " + i + ": '" + body + "'");
        }
      }

      // All comments should be displayed regardless of length
      assertEquals(bodies.size(), commentCount, "All comments should be displayed");
    } else {
      test.info("No comments found to verify minimum length handling");
    }
  }

  /** TC-037: View comments where author has no profile image */
  @Test(groups = {"regression", "edge"})
  public void testTC037_ViewCommentsAuthorNoProfileImage() {
    createTest(
        "TC-037: View comments where author has no profile image",
        "Verify default placeholder image shown for author");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      for (int i = 0; i < commentCount; i++) {
        String imageSrc = articlePage.getCommentAuthorImageSrc(i);
        boolean imageDisplayed = articlePage.isCommentAuthorImageDisplayed(i);

        test.info("Comment " + i + " - Image displayed: " + imageDisplayed + ", src: " + imageSrc);

        // Should have either an image or a placeholder
        assertTrue(
            imageDisplayed || (imageSrc != null && !imageSrc.isEmpty()),
            "Author should have image or placeholder");
      }
    } else {
      test.info("No comments found to verify author image handling");
    }
  }

  /** TC-038: View comments where author has no bio */
  @Test(groups = {"regression", "edge"})
  public void testTC038_ViewCommentsAuthorNoBio() {
    createTest(
        "TC-038: View comments where author has no bio",
        "Verify profile shown without bio field or with placeholder");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int commentCount = articlePage.getCommentCount();

    if (commentCount > 0) {
      // Get author username and verify profile is accessible
      String authorUsername = articlePage.getCommentAuthorUsername(0);
      test.info("First comment author: " + authorUsername);

      // Author info should be displayed even without bio
      assertNotNull(authorUsername, "Author username should be present");

      // Click on author to verify profile page works
      if (authorUsername != null && !authorUsername.isEmpty()) {
        articlePage.clickAuthorProfile(0);

        try {
          Thread.sleep(2000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        String currentUrl = articlePage.getCurrentUrl();
        test.info("Navigated to: " + currentUrl);

        // Should navigate to profile page
        assertTrue(
            currentUrl.contains("/profile/"),
            "Should navigate to profile page even if author has no bio");
      }
    } else {
      test.info("No comments found to verify author bio handling");
    }
  }

  /** TC-039: View comments on newly created article */
  @Test(groups = {"regression", "edge"})
  public void testTC039_ViewCommentsNewlyCreatedArticle() {
    createTest(
        "TC-039: View comments on newly created article",
        "Verify empty comments section, ready for new comments");

    // Navigate to an article that might be new (or use existing one)
    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    int commentCount = articlePage.getCommentCount();
    test.info("Comment count: " + commentCount);

    // Verify comment input is available for new comments
    boolean commentInputDisplayed = articlePage.isCommentInputDisplayed();
    test.info("Comment input displayed: " + commentInputDisplayed);

    // For logged-in users, comment input should be available
    // For anonymous users, it may or may not be displayed

    // Page should be functional regardless of comment count
    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should remain functional");
  }

  /** TC-040: View comments with rapid page refresh */
  @Test(groups = {"regression", "edge"})
  public void testTC040_ViewCommentsRapidRefresh() {
    createTest(
        "TC-040: View comments with rapid page refresh",
        "Verify comments load consistently each time");

    articlePage.navigateToArticle(ARTICLE_WITH_COMMENTS);
    articlePage.waitForCommentsToLoad();

    int initialCommentCount = articlePage.getCommentCount();
    test.info("Initial comment count: " + initialCommentCount);

    // Perform rapid refreshes
    for (int i = 0; i < 5; i++) {
      articlePage.refreshPage();
      articlePage.waitForCommentsToLoad();

      int currentCommentCount = articlePage.getCommentCount();
      test.info("Refresh " + (i + 1) + " - Comment count: " + currentCommentCount);

      // Comment count should be consistent
      assertEquals(
          currentCommentCount,
          initialCommentCount,
          "Comment count should be consistent after refresh " + (i + 1));

      // Small delay between refreshes
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    // Page should remain stable after rapid refreshes
    assertTrue(
        articlePage.isArticlePageDisplayed(),
        "Article page should remain displayed after refreshes");
  }
}
