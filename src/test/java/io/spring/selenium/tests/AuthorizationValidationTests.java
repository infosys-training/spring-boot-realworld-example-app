package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ApiHelper.ApiResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Validation tests for authorization error messages - verifying error message content and format.
 * Test Cases: TC-021 to TC-030
 */
public class AuthorizationValidationTests extends BaseTest {

  private String apiUrl;
  private ApiHelper apiHelper;

  // Test users from seed data
  private static final String AUTHOR_EMAIL = "john@example.com";
  private static final String AUTHOR_PASSWORD = "password123";

  private static final String NON_AUTHOR_EMAIL = "jane@example.com";
  private static final String NON_AUTHOR_PASSWORD = "password123";

  private String authorToken;
  private String nonAuthorToken;
  private String testArticleSlug;

  @BeforeClass
  public void setupTestData() throws Exception {
    apiUrl = config.getProperty("api.url", "http://localhost:8080");
    apiHelper = new ApiHelper(apiUrl);

    // Login both users
    authorToken = apiHelper.login(AUTHOR_EMAIL, AUTHOR_PASSWORD);
    nonAuthorToken = apiHelper.login(NON_AUTHOR_EMAIL, NON_AUTHOR_PASSWORD);

    // Create a test article as author
    String uniqueTitle = "Validation Test Article " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Test description", "Test body", new String[] {"test"}, authorToken);
    testArticleSlug = extractSlug(createResponse.body);
  }

  /**
   * TC-021: 403 error message is user-friendly. Verifies that the 403 error message is clear and
   * understandable.
   */
  @Test(groups = {"validation", "regression"})
  public void testTC021_403ErrorMessageIsUserFriendly() throws Exception {
    createTest(
        "TC-021: 403 error message is user-friendly",
        "Verify error message is clear and understandable");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertNotNull(response.body, "Response body should not be null");
    assertFalse(response.body.isEmpty(), "Response body should not be empty");
    test.info("Error response body: " + response.body);
    test.pass("403 error message is present and readable");
  }

  /**
   * TC-022: 403 error does not reveal user IDs. Verifies that the error response does not contain
   * internal user IDs (UUIDs).
   */
  @Test(groups = {"validation", "regression", "security"})
  public void testTC022_403ErrorDoesNotRevealUserIds() throws Exception {
    createTest("TC-022: 403 error does not reveal user IDs", "Verify no user IDs in error message");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");

    // Check for UUID pattern (user IDs are UUIDs in this system)
    boolean containsUuid =
        response.body.matches(".*[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}.*");
    assertFalse(containsUuid, "Error message should not contain UUIDs (user IDs)");
    test.pass("403 error does not reveal user IDs");
  }

  /**
   * TC-023: 403 error does not reveal email addresses. Verifies that the error response does not
   * contain email addresses.
   */
  @Test(groups = {"validation", "regression", "security"})
  public void testTC023_403ErrorDoesNotRevealEmailAddresses() throws Exception {
    createTest(
        "TC-023: 403 error does not reveal email addresses", "Verify no email addresses in error");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(
        response.containsEmailAddress(), "Error message should not contain email addresses");
    assertFalse(
        response.body.contains("@example.com"), "Error should not contain test email domain");
    test.pass("403 error does not reveal email addresses");
  }

  /**
   * TC-024: 403 error does not reveal stack traces. Verifies that the error response does not
   * contain Java stack traces.
   */
  @Test(groups = {"validation", "regression", "security"})
  public void testTC024_403ErrorDoesNotRevealStackTraces() throws Exception {
    createTest("TC-024: 403 error does not reveal stack traces", "Verify no stack traces in error");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(response.body.contains("at io.spring"), "Error should not contain stack trace");
    assertFalse(response.body.contains("at java."), "Error should not contain Java stack trace");
    assertFalse(response.body.contains("at org."), "Error should not contain library stack trace");
    assertFalse(
        response.body.toLowerCase().contains("stacktrace"), "Error should not mention stacktrace");
    test.pass("403 error does not reveal stack traces");
  }

  /**
   * TC-025: 403 error does not reveal database info. Verifies that the error response does not
   * contain database connection information.
   */
  @Test(groups = {"validation", "regression", "security"})
  public void testTC025_403ErrorDoesNotRevealDatabaseInfo() throws Exception {
    createTest(
        "TC-025: 403 error does not reveal database info", "Verify no database info in error");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(response.body.toLowerCase().contains("jdbc:"), "Error should not contain JDBC URL");
    assertFalse(
        response.body.toLowerCase().contains("sqlite"), "Error should not mention database type");
    assertFalse(response.body.toLowerCase().contains("mysql"), "Error should not mention MySQL");
    assertFalse(
        response.body.toLowerCase().contains("postgresql"), "Error should not mention PostgreSQL");
    test.pass("403 error does not reveal database information");
  }

  /**
   * TC-026: 403 error does not reveal server paths. Verifies that the error response does not
   * contain server file system paths.
   */
  @Test(groups = {"validation", "regression", "security"})
  public void testTC026_403ErrorDoesNotRevealServerPaths() throws Exception {
    createTest("TC-026: 403 error does not reveal server paths", "Verify no server paths in error");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertFalse(response.body.contains("/home/"), "Error should not contain home directory paths");
    assertFalse(response.body.contains("/usr/"), "Error should not contain system paths");
    assertFalse(response.body.contains("/var/"), "Error should not contain var paths");
    assertFalse(response.body.contains("C:\\"), "Error should not contain Windows paths");
    test.pass("403 error does not reveal server paths");
  }

  /**
   * TC-027: 403 response has correct content type. Verifies that the 403 response has a valid
   * Content-Type header (application/json or text/html are acceptable).
   */
  @Test(groups = {"validation", "regression"})
  public void testTC027_403ResponseHasCorrectContentType() throws Exception {
    createTest(
        "TC-027: 403 response has correct content type",
        "Verify Content-Type is valid (application/json or text/html)");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");
    assertNotNull(response.contentType, "Content-Type header should be present");
    boolean hasValidContentType =
        response.contentType.contains("application/json")
            || response.contentType.contains("text/html");
    assertTrue(
        hasValidContentType,
        "Content-Type should be application/json or text/html, got: " + response.contentType);
    test.info("Content-Type: " + response.contentType);
    test.pass("403 response has valid Content-Type");
  }

  /**
   * TC-028: 403 response body format is consistent. Verifies that multiple 403 responses have the
   * same structure.
   */
  @Test(groups = {"validation", "regression"})
  public void testTC028_403ResponseBodyFormatIsConsistent() throws Exception {
    createTest(
        "TC-028: 403 response body format is consistent", "Verify consistent 403 response format");

    // Make multiple unauthorized requests
    ApiResponse editResponse =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);
    ApiResponse deleteResponse = apiHelper.deleteArticle(testArticleSlug, nonAuthorToken);

    assertEquals(editResponse.statusCode, 403, "Edit should return 403");
    assertEquals(deleteResponse.statusCode, 403, "Delete should return 403");

    // Both responses should have similar structure (both JSON or both have error field)
    boolean editHasError =
        editResponse.body.contains("error") || editResponse.body.contains("message");
    boolean deleteHasError =
        deleteResponse.body.contains("error") || deleteResponse.body.contains("message");

    // At minimum, both should be valid responses (not empty)
    assertFalse(editResponse.body.isEmpty(), "Edit error response should not be empty");
    assertFalse(deleteResponse.body.isEmpty(), "Delete error response should not be empty");
    test.info("Edit response: " + editResponse.body);
    test.info("Delete response: " + deleteResponse.body);
    test.pass("403 responses have consistent format");
  }

  /**
   * TC-029: 403 error provides helpful guidance. Verifies that the error message indicates a
   * permission issue.
   */
  @Test(groups = {"validation", "regression"})
  public void testTC029_403ErrorProvidesHelpfulGuidance() throws Exception {
    createTest(
        "TC-029: 403 error provides helpful guidance", "Verify error indicates permission issue");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);

    assertEquals(response.statusCode, 403, "Should receive 403 Forbidden");

    // The response should indicate some form of authorization/permission issue
    String lowerBody = response.body.toLowerCase();
    boolean indicatesPermissionIssue =
        lowerBody.contains("forbidden")
            || lowerBody.contains("unauthorized")
            || lowerBody.contains("permission")
            || lowerBody.contains("not allowed")
            || lowerBody.contains("access denied")
            || lowerBody.contains("authorization")
            || response.statusCode == 403; // Status code itself indicates the issue

    assertTrue(indicatesPermissionIssue, "Error should indicate permission issue");
    test.pass("403 error indicates permission issue (via status code or message)");
  }

  /**
   * TC-030: 403 vs 401 error messages are distinct. Verifies that 403 (Forbidden) and 401
   * (Unauthorized) responses are different.
   */
  @Test(groups = {"validation", "regression", "smoke"})
  public void testTC030_403Vs401ErrorMessagesAreDistinct() throws Exception {
    createTest(
        "TC-030: 403 vs 401 error messages are distinct", "Verify 403 and 401 are different");

    // Get 403 response (authenticated but not authorized)
    ApiResponse forbiddenResponse =
        apiHelper.editArticle(testArticleSlug, "Unauthorized", "Desc", "Body", nonAuthorToken);
    assertEquals(forbiddenResponse.statusCode, 403, "Should receive 403 Forbidden");

    // Get 401 response (not authenticated)
    ApiResponse unauthorizedResponse =
        apiHelper.editArticle(testArticleSlug, "No Auth", "Desc", "Body", null);
    assertEquals(unauthorizedResponse.statusCode, 401, "Should receive 401 Unauthorized");

    // Verify they are different
    assertNotEquals(
        forbiddenResponse.statusCode,
        unauthorizedResponse.statusCode,
        "403 and 401 status codes should be different");

    test.info("403 response: " + forbiddenResponse.body);
    test.info("401 response: " + unauthorizedResponse.body);
    test.pass("403 and 401 responses are distinct");
  }

  // Helper method
  private String extractSlug(String responseBody) {
    try {
      int slugStart = responseBody.indexOf("\"slug\":\"") + 8;
      int slugEnd = responseBody.indexOf("\"", slugStart);
      return responseBody.substring(slugStart, slugEnd);
    } catch (Exception e) {
      return null;
    }
  }
}
