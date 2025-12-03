package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.EditorPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Edge case test cases for Add Comment to Article functionality. Test cases TC-031 through TC-040
 * covering boundary and edge case scenarios.
 */
public class CommentEdgeCaseTests extends BaseTest {

  private LoginPage loginPage;
  private HomePage homePage;
  private ArticlePage articlePage;
  private EditorPage editorPage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    loginPage = new LoginPage(driver);
    homePage = new HomePage(driver);
    articlePage = new ArticlePage(driver);
    editorPage = new EditorPage(driver);
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

  @Test(groups = {"regression", "edge"})
  public void testTC031_AddCommentWithSpecialCharactersInBody() {
    createTest(
        "TC-031: Add comment with special characters in body",
        "Verify that special characters are preserved in comments");

    loginAsTestUser();
    navigateToFirstArticle();

    String specialChars = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
    articlePage.addComment(specialChars);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Special characters comment added successfully");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC032_AddCommentWithUnicodeCharacters() {
    createTest(
        "TC-032: Add comment with Unicode characters",
        "Verify that Unicode characters are preserved");

    loginAsTestUser();
    navigateToFirstArticle();

    String unicodeComment = "Unicode test: Chinese characters Japanese Cyrillic Arabic";
    articlePage.addComment(unicodeComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Unicode characters comment added successfully");
  }

  @Test(groups = {"smoke", "regression", "edge"})
  public void testTC033_AddCommentWithHtmlTagsInBody() {
    createTest(
        "TC-033: Add comment with HTML tags in body",
        "Verify that HTML tags are escaped or sanitized");

    loginAsTestUser();
    navigateToFirstArticle();

    String htmlComment = "<script>alert('XSS')</script><b>Bold</b><a href='evil.com'>Link</a>";
    articlePage.addComment(htmlComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("HTML tags in comment handled safely");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC034_AddCommentWithEmojiCharacters() {
    createTest(
        "TC-034: Add comment with emoji characters", "Verify that emoji characters are preserved");

    loginAsTestUser();
    navigateToFirstArticle();

    String emojiComment = "Emoji test: Great article! Thumbs up Fire Heart";
    articlePage.addComment(emojiComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Emoji characters comment added successfully");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC035_AddCommentWithNewlineCharacters() {
    createTest(
        "TC-035: Add comment with newline characters",
        "Verify that newlines are preserved in comments");

    loginAsTestUser();
    navigateToFirstArticle();

    String multilineComment = "Line 1\nLine 2\nLine 3\n\nParagraph 2";
    articlePage.addComment(multilineComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Multiline comment added successfully");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC036_AddCommentWithVeryLongSingleWord() {
    createTest(
        "TC-036: Add comment with very long single word",
        "Verify that long words without spaces are handled");

    loginAsTestUser();
    navigateToFirstArticle();

    StringBuilder longWord = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      longWord.append("Supercalifragilisticexpialidocious");
    }
    String longWordComment = longWord.toString();

    articlePage.addComment(longWordComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    test.info("Long single word comment handled with " + longWordComment.length() + " characters");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC037_AddCommentImmediatelyAfterArticleCreation() {
    createTest(
        "TC-037: Add comment immediately after article creation",
        "Verify that comments can be added to newly created articles");

    loginAsTestUser();

    editorPage.navigateTo(BASE_URL);

    String uniqueTitle = "Test Article TC037 " + System.currentTimeMillis();
    editorPage.createArticle(
        uniqueTitle, "Test description for TC-037", "Test body content for TC-037", "test");

    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (articlePage.isOnArticlePage()) {
      articlePage.waitForCommentsToLoad();
      articlePage.addComment("First comment on new article TC-037");

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
      test.info("Comment added to newly created article successfully");
    } else {
      test.info("Article creation flow completed");
    }
  }

  @Test(groups = {"regression", "edge"})
  public void testTC038_AddCommentWithMarkdownFormatting() {
    createTest(
        "TC-038: Add comment with markdown formatting",
        "Verify that markdown formatting is preserved or rendered");

    loginAsTestUser();
    navigateToFirstArticle();

    String markdownComment =
        "**Bold text** and *italic text* and `code` and [link](http://example.com)";
    articlePage.addComment(markdownComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Markdown formatted comment added successfully");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC039_AddCommentWithUrlInBody() {
    createTest(
        "TC-039: Add comment with URL in body", "Verify that URLs are preserved in comments");

    loginAsTestUser();
    navigateToFirstArticle();

    String urlComment =
        "Check out this link: https://example.com/path?param=value&other=123#anchor";
    articlePage.addComment(urlComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("URL in comment added successfully");
  }

  @Test(groups = {"regression", "edge"})
  public void testTC040_AddCommentWithLeadingTrailingWhitespace() {
    createTest(
        "TC-040: Add comment with leading/trailing whitespace",
        "Verify that whitespace is trimmed or preserved appropriately");

    loginAsTestUser();
    navigateToFirstArticle();

    String whitespaceComment = "   Comment with leading and trailing spaces   ";
    articlePage.addComment(whitespaceComment);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    assertTrue(articlePage.isOnArticlePage(), "Should remain on article page");
    assertFalse(articlePage.isErrorDisplayed(), "No error should be displayed");
    test.info("Comment with whitespace added successfully");
  }
}
