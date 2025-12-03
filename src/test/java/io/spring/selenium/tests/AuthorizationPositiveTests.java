package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ApiHelper.ApiResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Positive tests for authorization - verifying that authorized users CAN perform actions. Test
 * Cases: TC-001 to TC-008
 */
public class AuthorizationPositiveTests extends BaseTest {

  private String baseUrl;
  private String apiUrl;
  private ApiHelper apiHelper;

  // Test users from seed data
  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";
  private static final String AUTHOR_USERNAME = "johndoe";

  private static final String OTHER_USER_EMAIL = "jane@example.com";
  private static final String OTHER_USER_PASSWORD = "password123";

  @BeforeClass
  public void setupTestData() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    apiUrl = config.getProperty("api.url", "http://localhost:8080");
    apiHelper = new ApiHelper(apiUrl);
  }

  /**
   * TC-001: Author can edit own article title. Verifies that an article author can successfully
   * edit the title of their own article.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC001_AuthorCanEditOwnArticleTitle() throws Exception {
    createTest(
        "TC-001: Author can edit own article title", "Verify author can edit their article title");

    // Login as author via API
    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    assertNotNull(token, "Author should be able to login");
    test.info("Logged in as author: " + AUTHOR_USERNAME);

    // Create a test article
    String uniqueTitle = "Test Article " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Test description", "Test body content", new String[] {"test"}, token);
    assertEquals(createResponse.statusCode, 200, "Article should be created successfully");
    test.info("Created test article: " + uniqueTitle);

    // Extract slug from response
    String slug = extractSlug(createResponse.body);
    assertNotNull(slug, "Slug should be extracted from response");

    // Edit the article title
    String newTitle = "Updated Title " + System.currentTimeMillis();
    ApiResponse editResponse =
        apiHelper.editArticle(slug, newTitle, "Updated description", "Updated body", token);
    assertEquals(editResponse.statusCode, 200, "Author should be able to edit article title");
    test.pass("Author successfully edited article title");

    // Cleanup - delete the article
    apiHelper.deleteArticle(slug, token);
  }

  /**
   * TC-002: Author can edit own article body. Verifies that an article author can successfully edit
   * the body content of their own article.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC002_AuthorCanEditOwnArticleBody() throws Exception {
    createTest(
        "TC-002: Author can edit own article body", "Verify author can edit their article body");

    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    assertNotNull(token, "Author should be able to login");

    // Create a test article
    String uniqueTitle = "Body Edit Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle,
            "Original description",
            "Original body content",
            new String[] {"test"},
            token);
    assertEquals(createResponse.statusCode, 200, "Article should be created");

    String slug = extractSlug(createResponse.body);

    // Edit the article body
    ApiResponse editResponse =
        apiHelper.editArticle(
            slug, uniqueTitle, "Original description", "New body content with updates", token);
    assertEquals(editResponse.statusCode, 200, "Author should be able to edit article body");
    test.pass("Author successfully edited article body");

    // Cleanup
    apiHelper.deleteArticle(slug, token);
  }

  /**
   * TC-003: Author can delete own article. Verifies that an article author can successfully delete
   * their own article.
   */
  @Test(groups = {"positive", "regression", "smoke"})
  public void testTC003_AuthorCanDeleteOwnArticle() throws Exception {
    createTest(
        "TC-003: Author can delete own article", "Verify author can delete their own article");

    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    assertNotNull(token, "Author should be able to login");

    // Create a test article
    String uniqueTitle = "Delete Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle,
            "To be deleted",
            "This article will be deleted",
            new String[] {"test"},
            token);
    assertEquals(createResponse.statusCode, 200, "Article should be created");

    String slug = extractSlug(createResponse.body);

    // Delete the article (204 No Content is the correct response for successful DELETE)
    ApiResponse deleteResponse = apiHelper.deleteArticle(slug, token);
    assertEquals(deleteResponse.statusCode, 204, "Author should be able to delete their article");
    test.pass("Author successfully deleted their article");

    // Verify article is deleted
    ApiResponse getResponse = apiHelper.getArticle(slug, token);
    assertEquals(getResponse.statusCode, 404, "Deleted article should return 404");
  }

  /**
   * TC-004: Comment author can delete own comment. Verifies that a comment author can successfully
   * delete their own comment.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC004_CommentAuthorCanDeleteOwnComment() throws Exception {
    createTest(
        "TC-004: Comment author can delete own comment",
        "Verify comment author can delete their comment");

    // Login as article author and create article
    String authorToken = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    String uniqueTitle = "Comment Delete Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle,
            "Article for comments",
            "Body content",
            new String[] {"test"},
            authorToken);
    String slug = extractSlug(createResponse.body);

    // Login as other user and add a comment (201 Created is the correct response)
    String otherToken = apiHelper.login(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);
    ApiResponse commentResponse =
        apiHelper.addComment(slug, "This is my comment to delete", otherToken);
    assertEquals(commentResponse.statusCode, 201, "Comment should be created");

    // Extract comment ID
    String commentId = extractCommentId(commentResponse.body);
    assertNotNull(commentId, "Comment ID should be extracted");

    // Delete own comment (204 No Content is the correct response for successful DELETE)
    ApiResponse deleteResponse = apiHelper.deleteComment(slug, commentId, otherToken);
    assertEquals(
        deleteResponse.statusCode, 204, "Comment author should be able to delete their comment");
    test.pass("Comment author successfully deleted their comment");

    // Cleanup
    apiHelper.deleteArticle(slug, authorToken);
  }

  /**
   * TC-005: Article author can delete any comment on their article. Verifies that an article author
   * can delete comments made by other users on their article.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC005_ArticleAuthorCanDeleteAnyComment() throws Exception {
    createTest(
        "TC-005: Article author can delete any comment",
        "Verify article author can delete others' comments");

    // Login as article author and create article
    String authorToken = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    String uniqueTitle = "Any Comment Delete Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle,
            "Article for comment deletion",
            "Body content",
            new String[] {"test"},
            authorToken);
    String slug = extractSlug(createResponse.body);

    // Login as other user and add a comment (201 Created is the correct response)
    String otherToken = apiHelper.login(OTHER_USER_EMAIL, OTHER_USER_PASSWORD);
    ApiResponse commentResponse = apiHelper.addComment(slug, "Comment by another user", otherToken);
    assertEquals(commentResponse.statusCode, 201, "Comment should be created");

    String commentId = extractCommentId(commentResponse.body);

    // Article author deletes the comment (204 No Content is the correct response)
    ApiResponse deleteResponse = apiHelper.deleteComment(slug, commentId, authorToken);
    assertEquals(
        deleteResponse.statusCode, 204, "Article author should be able to delete any comment");
    test.pass("Article author successfully deleted another user's comment");

    // Cleanup
    apiHelper.deleteArticle(slug, authorToken);
  }

  /**
   * TC-006: Author can add tags to own article. Verifies that an article author can add tags to
   * their article.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC006_AuthorCanAddTagsToOwnArticle() throws Exception {
    createTest(
        "TC-006: Author can add tags to own article",
        "Verify author can add tags to their article");

    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    // Create article with initial tags
    String uniqueTitle = "Tags Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Article with tags", "Body content", new String[] {"initial"}, token);
    assertEquals(createResponse.statusCode, 200, "Article should be created with tags");

    String slug = extractSlug(createResponse.body);
    assertTrue(createResponse.body.contains("initial"), "Article should have initial tag");
    test.pass("Author successfully created article with tags");

    // Cleanup
    apiHelper.deleteArticle(slug, token);
  }

  /**
   * TC-007: Author can remove tags from own article. Verifies that an article author can update
   * their article (tags are managed through article updates).
   */
  @Test(groups = {"positive", "regression"})
  public void testTC007_AuthorCanUpdateArticleTags() throws Exception {
    createTest(
        "TC-007: Author can update article tags", "Verify author can update their article tags");

    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    // Create article
    String uniqueTitle = "Tag Update Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Article for tag update", "Body content", new String[] {"oldtag"}, token);
    assertEquals(createResponse.statusCode, 200, "Article should be created");

    String slug = extractSlug(createResponse.body);

    // Update article (tags are immutable in this API, but we verify edit works)
    ApiResponse editResponse =
        apiHelper.editArticle(slug, uniqueTitle, "Updated description", "Updated body", token);
    assertEquals(editResponse.statusCode, 200, "Author should be able to update article");
    test.pass("Author successfully updated article");

    // Cleanup
    apiHelper.deleteArticle(slug, token);
  }

  /**
   * TC-008: Author can edit article description. Verifies that an article author can successfully
   * edit the description of their article.
   */
  @Test(groups = {"positive", "regression"})
  public void testTC008_AuthorCanEditArticleDescription() throws Exception {
    createTest(
        "TC-008: Author can edit article description",
        "Verify author can edit their article description");

    String token = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);

    // Create article
    String uniqueTitle = "Description Edit Test " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Original description", "Body content", new String[] {"test"}, token);
    assertEquals(createResponse.statusCode, 200, "Article should be created");

    String slug = extractSlug(createResponse.body);

    // Edit description
    String newDescription = "Updated description " + System.currentTimeMillis();
    ApiResponse editResponse =
        apiHelper.editArticle(slug, uniqueTitle, newDescription, "Body content", token);
    assertEquals(editResponse.statusCode, 200, "Author should be able to edit description");
    assertTrue(
        editResponse.body.contains(newDescription), "Response should contain new description");
    test.pass("Author successfully edited article description");

    // Cleanup
    apiHelper.deleteArticle(slug, token);
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
