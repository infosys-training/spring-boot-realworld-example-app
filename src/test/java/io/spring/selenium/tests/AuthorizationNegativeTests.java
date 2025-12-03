package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ApiHelper.ApiResponse;
import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Negative tests for authorization - verifying 403 Forbidden responses for unauthorized actions.
 * Test Cases: TC-009 to TC-020
 */
public class AuthorizationNegativeTests extends BaseTest {

  private String baseUrl;
  private String apiUrl;
  private ApiHelper apiHelper;

  // Test users from seed data
  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";
  private static final String AUTHOR_USERNAME = "johndoe";

  private static final String NON_AUTHOR_EMAIL = "jane@example.com";
  private static final String NON_AUTHOR_PASSWORD = "password123";
  private static final String NON_AUTHOR_USERNAME = "janedoe";

  private String authorToken;
  private String nonAuthorToken;
  private String testArticleSlug;

  @BeforeClass
  public void setupTestData() throws Exception {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    apiUrl = config.getProperty("api.url", "http://localhost:8080");
    apiHelper = new ApiHelper(apiUrl);

    // Login both users
    authorToken = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    nonAuthorToken = apiHelper.login(NON_AUTHOR_EMAIL, NON_AUTHOR_PASSWORD);

    // Create a test article as author
    String uniqueTitle = "Authorization Test Article " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Test description", "Test body", new String[] {"test"}, authorToken);
    testArticleSlug = extractSlug(createResponse.body);
  }

  /**
   * TC-009: Non-author cannot edit article title. Verifies that a non-author receives 403 Forbidden
   * when attempting to edit another user's article title.
   */
  @Test(groups = {"negative", "regression", "smoke"})
  public void testTC009_NonAuthorCannotEditArticleTitle() throws Exception {
    createTest(
        "TC-009: Non-author cannot edit article title", "Verify 403 when non-author edits title");

    ApiResponse response =
        apiHelper.editArticle(
            testArticleSlug, "Unauthorized Title Change", null, null, nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to edit article title");
  }

  /**
   * TC-010: Non-author cannot edit article body. Verifies that a non-author receives 403 Forbidden
   * when attempting to edit another user's article body.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC010_NonAuthorCannotEditArticleBody() throws Exception {
    createTest(
        "TC-010: Non-author cannot edit article body", "Verify 403 when non-author edits body");

    ApiResponse response =
        apiHelper.editArticle(
            testArticleSlug, null, null, "Unauthorized body change", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to edit article body");
  }

  /**
   * TC-011: Non-author cannot edit article description. Verifies that a non-author receives 403
   * Forbidden when attempting to edit another user's article description.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC011_NonAuthorCannotEditArticleDescription() throws Exception {
    createTest(
        "TC-011: Non-author cannot edit article description",
        "Verify 403 when non-author edits description");

    ApiResponse response =
        apiHelper.editArticle(
            testArticleSlug, null, "Unauthorized description", null, nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass(
        "Non-author correctly received 403 Forbidden when trying to edit article description");
  }

  /**
   * TC-012: Non-author cannot delete article. Verifies that a non-author receives 403 Forbidden
   * when attempting to delete another user's article.
   */
  @Test(groups = {"negative", "regression", "smoke"})
  public void testTC012_NonAuthorCannotDeleteArticle() throws Exception {
    createTest(
        "TC-012: Non-author cannot delete article", "Verify 403 when non-author deletes article");

    ApiResponse response = apiHelper.deleteArticle(testArticleSlug, nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to delete article");

    // Verify article still exists
    ApiResponse getResponse = apiHelper.getArticle(testArticleSlug, authorToken);
    assertEquals(
        getResponse.statusCode,
        200,
        "Article should still exist after unauthorized delete attempt");
  }

  /**
   * TC-013: Non-author cannot delete others' comment. Verifies that a user who is neither the
   * comment author nor article author receives 403 Forbidden.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC013_NonAuthorCannotDeleteOthersComment() throws Exception {
    createTest(
        "TC-013: Non-author cannot delete others' comment",
        "Verify 403 when deleting others' comment");

    // Author adds a comment to their own article (201 Created is the correct response)
    ApiResponse commentResponse =
        apiHelper.addComment(testArticleSlug, "Author's comment", authorToken);
    assertEquals(commentResponse.statusCode, 201, "Comment should be created");
    String commentId = extractCommentId(commentResponse.body);

    // Non-author tries to delete the comment
    ApiResponse deleteResponse =
        apiHelper.deleteComment(testArticleSlug, commentId, nonAuthorToken);
    assertEquals(deleteResponse.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to delete others' comment");

    // Cleanup - author deletes their own comment
    apiHelper.deleteComment(testArticleSlug, commentId, authorToken);
  }

  /**
   * TC-014: Edit button hidden for non-author in UI. Verifies that the Edit button is not visible
   * to non-authors viewing an article.
   */
  @Test(groups = {"negative", "regression", "ui"})
  public void testTC014_EditButtonHiddenForNonAuthor() throws Exception {
    createTest(
        "TC-014: Edit button hidden for non-author",
        "Verify Edit button not visible for non-author");

    // Login as non-author via UI
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);
    loginPage.login(NON_AUTHOR_EMAIL, NON_AUTHOR_PASSWORD);

    // Wait for login to complete
    Thread.sleep(2000);

    // Navigate to author's article
    ArticlePage articlePage = new ArticlePage(driver);
    articlePage.navigateTo(baseUrl, testArticleSlug);
    articlePage.waitForPageLoad();

    // Verify edit button is not visible
    assertFalse(
        articlePage.isEditButtonVisible(), "Edit button should not be visible for non-author");
    test.pass("Edit button correctly hidden for non-author");
  }

  /**
   * TC-015: Delete button hidden for non-author in UI. Verifies that the Delete button is not
   * visible to non-authors viewing an article.
   */
  @Test(groups = {"negative", "regression", "ui"})
  public void testTC015_DeleteButtonHiddenForNonAuthor() throws Exception {
    createTest(
        "TC-015: Delete button hidden for non-author",
        "Verify Delete button not visible for non-author");

    // Login as non-author via UI
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);
    loginPage.login(NON_AUTHOR_EMAIL, NON_AUTHOR_PASSWORD);

    Thread.sleep(2000);

    // Navigate to author's article
    ArticlePage articlePage = new ArticlePage(driver);
    articlePage.navigateTo(baseUrl, testArticleSlug);
    articlePage.waitForPageLoad();

    // Verify delete button is not visible
    assertFalse(
        articlePage.isDeleteButtonVisible(), "Delete button should not be visible for non-author");
    test.pass("Delete button correctly hidden for non-author");
  }

  /**
   * TC-016: Delete comment button hidden for non-author. Verifies that the delete button for
   * comments is not visible to users who are neither the comment author nor article author.
   */
  @Test(groups = {"negative", "regression", "ui"})
  public void testTC016_DeleteCommentButtonHiddenForNonAuthor() throws Exception {
    createTest(
        "TC-016: Delete comment button hidden for non-author",
        "Verify comment delete button not visible for non-author");

    // Create a third user's comment scenario
    // Author adds a comment
    ApiResponse commentResponse =
        apiHelper.addComment(testArticleSlug, "Test comment for UI", authorToken);
    String commentId = extractCommentId(commentResponse.body);

    // Login as non-author via UI
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(baseUrl);
    loginPage.login(NON_AUTHOR_EMAIL, NON_AUTHOR_PASSWORD);

    Thread.sleep(2000);

    // Navigate to article with comment
    ArticlePage articlePage = new ArticlePage(driver);
    articlePage.navigateTo(baseUrl, testArticleSlug);
    articlePage.waitForPageLoad();

    Thread.sleep(1000);

    // Verify delete button is not visible for author's comment
    assertFalse(
        articlePage.isCommentDeleteButtonVisible(0),
        "Delete button should not be visible for others' comments");
    test.pass("Comment delete button correctly hidden for non-author");

    // Cleanup
    apiHelper.deleteComment(testArticleSlug, commentId, authorToken);
  }

  /**
   * TC-017: Non-author cannot add tags to article. Verifies that a non-author receives 403
   * Forbidden when attempting to modify article tags.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC017_NonAuthorCannotAddTagsToArticle() throws Exception {
    createTest(
        "TC-017: Non-author cannot add tags to article",
        "Verify 403 when non-author modifies tags");

    // Attempt to edit article (which would include tag changes)
    ApiResponse response =
        apiHelper.editArticle(
            testArticleSlug, "Title with new tags", "Description", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to modify article");
  }

  /**
   * TC-018: Non-author cannot remove tags from article. Verifies that a non-author receives 403
   * Forbidden when attempting to update article (including tag removal).
   */
  @Test(groups = {"negative", "regression"})
  public void testTC018_NonAuthorCannotRemoveTagsFromArticle() throws Exception {
    createTest(
        "TC-018: Non-author cannot remove tags from article",
        "Verify 403 when non-author removes tags");

    ApiResponse response =
        apiHelper.editArticle(
            testArticleSlug, "Updated title", "Updated desc", "Updated body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Non-author should receive 403 Forbidden");
    test.pass("Non-author correctly received 403 Forbidden when trying to update article");
  }

  /**
   * TC-019: Non-author edit shows error message. Verifies that the 403 response includes an
   * appropriate error message without sensitive information.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC019_NonAuthorEditShowsErrorMessage() throws Exception {
    createTest(
        "TC-019: Non-author edit shows error message", "Verify error message on unauthorized edit");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized edit", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(
        response.containsSensitiveInfo(), "Error message should not contain sensitive information");
    test.pass("Error message displayed without sensitive information");
  }

  /**
   * TC-020: Non-author delete shows error message. Verifies that the 403 response for delete
   * includes an appropriate error message without sensitive information.
   */
  @Test(groups = {"negative", "regression"})
  public void testTC020_NonAuthorDeleteShowsErrorMessage() throws Exception {
    createTest(
        "TC-020: Non-author delete shows error message",
        "Verify error message on unauthorized delete");

    ApiResponse response = apiHelper.deleteArticle(testArticleSlug, nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(
        response.containsSensitiveInfo(), "Error message should not contain sensitive information");
    test.pass("Error message displayed without sensitive information");
  }

  // Helper methods
  private String extractSlug(String responseBody) {
    try {
      int slugStart = responseBody.indexOf("\"slug\":\"") + 8;
      int slugEnd = responseBody.indexOf("\"", slugStart);
      return responseBody.substring(slugStart, slugEnd);
    } catch (Exception e) {
      return null;
    }
  }

  private String extractCommentId(String responseBody) {
    try {
      // Comment ID is a UUID string in the format: "id":"uuid-value"
      int idStart = responseBody.indexOf("\"id\":\"") + 6;
      int idEnd = responseBody.indexOf("\"", idStart);
      return responseBody.substring(idStart, idEnd).trim();
    } catch (Exception e) {
      return null;
    }
  }
}
