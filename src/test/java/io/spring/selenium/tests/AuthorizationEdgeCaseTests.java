package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ApiHelper.ApiResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Edge case tests for authorization - testing boundary conditions, authentication vs authorization,
 * and special scenarios. Test Cases: TC-031 to TC-040
 */
public class AuthorizationEdgeCaseTests extends BaseTest {

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
    String uniqueTitle = "Edge Case Test Article " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            uniqueTitle, "Test description", "Test body", new String[] {"test"}, authorToken);
    testArticleSlug = extractSlug(createResponse.body);
  }

  /**
   * TC-031: Unauthenticated edit returns 401. Verifies that attempting to edit an article without
   * authentication returns 401 Unauthorized.
   */
  @Test(groups = {"edge", "regression", "smoke"})
  public void testTC031_UnauthenticatedEditReturns401() throws Exception {
    createTest("TC-031: Unauthenticated edit returns 401", "Verify 401 for unauthenticated edit");

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "No Auth Edit", "Desc", "Body", null);

    assertEquals(response.statusCode, 401, "Unauthenticated edit should return 401 Unauthorized");
    test.pass("Unauthenticated edit correctly returns 401 Unauthorized");
  }

  /**
   * TC-032: Unauthenticated delete returns 401. Verifies that attempting to delete an article
   * without authentication returns 401 Unauthorized.
   */
  @Test(groups = {"edge", "regression", "smoke"})
  public void testTC032_UnauthenticatedDeleteReturns401() throws Exception {
    createTest(
        "TC-032: Unauthenticated delete returns 401", "Verify 401 for unauthenticated delete");

    ApiResponse response = apiHelper.deleteArticle(testArticleSlug, null);

    assertEquals(response.statusCode, 401, "Unauthenticated delete should return 401 Unauthorized");
    test.pass("Unauthenticated delete correctly returns 401 Unauthorized");
  }

  /**
   * TC-033: Expired token returns 401 not 403. Verifies that an expired JWT token returns 401
   * Unauthorized, not 403 Forbidden.
   */
  @Test(groups = {"edge", "regression"})
  public void testTC033_ExpiredTokenReturns401Not403() throws Exception {
    createTest("TC-033: Expired token returns 401 not 403", "Verify expired token returns 401");

    // Use a clearly expired/invalid token (simulated expired token)
    String expiredToken =
        "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.invalid";

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Expired Token Edit", "Desc", "Body", expiredToken);

    // Should be 401 (authentication failure) not 403 (authorization failure)
    assertEquals(response.statusCode, 401, "Expired token should return 401, not 403");
    test.pass("Expired token correctly returns 401 Unauthorized");
  }

  /**
   * TC-034: Invalid token returns 401 not 403. Verifies that a malformed JWT token returns 401
   * Unauthorized, not 403 Forbidden.
   */
  @Test(groups = {"edge", "regression"})
  public void testTC034_InvalidTokenReturns401Not403() throws Exception {
    createTest("TC-034: Invalid token returns 401 not 403", "Verify invalid token returns 401");

    // Use a clearly invalid/malformed token
    String invalidToken = "this-is-not-a-valid-jwt-token";

    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Invalid Token Edit", "Desc", "Body", invalidToken);

    // Should be 401 (authentication failure) not 403 (authorization failure)
    assertEquals(response.statusCode, 401, "Invalid token should return 401, not 403");
    test.pass("Invalid token correctly returns 401 Unauthorized");
  }

  /**
   * TC-035: Valid token wrong user returns 403. Verifies that a valid token for a different user
   * returns 403 Forbidden (not 401).
   */
  @Test(groups = {"edge", "regression", "smoke"})
  public void testTC035_ValidTokenWrongUserReturns403() throws Exception {
    createTest(
        "TC-035: Valid token wrong user returns 403",
        "Verify valid token for wrong user returns 403");

    // Use non-author's valid token to edit author's article
    ApiResponse response =
        apiHelper.editArticle(testArticleSlug, "Wrong User Edit", "Desc", "Body", nonAuthorToken);

    assertEquals(
        response.statusCode, 403, "Valid token for wrong user should return 403 Forbidden");
    assertNotEquals(response.statusCode, 401, "Should not return 401 for valid token");
    test.pass("Valid token for wrong user correctly returns 403 Forbidden");
  }

  /**
   * TC-036: Authorization check with special chars in slug. Verifies that authorization works
   * correctly for articles with special characters in the slug.
   */
  @Test(groups = {"edge", "regression"})
  public void testTC036_AuthorizationCheckWithSpecialCharsInSlug() throws Exception {
    createTest(
        "TC-036: Authorization check with special chars in slug",
        "Verify 403 for article with special chars");

    // Create article with special characters in title (which affects slug)
    String specialTitle =
        "Test Article with Special Chars & Symbols! " + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            specialTitle, "Description", "Body", new String[] {"test"}, authorToken);
    assertEquals(createResponse.statusCode, 200, "Article with special chars should be created");

    String specialSlug = extractSlug(createResponse.body);
    assertNotNull(specialSlug, "Slug should be extracted");

    // Non-author tries to edit
    ApiResponse editResponse =
        apiHelper.editArticle(specialSlug, "Unauthorized Edit", "Desc", "Body", nonAuthorToken);
    assertEquals(editResponse.statusCode, 403, "Should return 403 for special char slug article");
    test.pass("Authorization correctly enforced for article with special characters in slug");

    // Cleanup
    apiHelper.deleteArticle(specialSlug, authorToken);
  }

  /**
   * TC-037: Authorization check with very long slug. Verifies that authorization works correctly
   * for articles with very long slugs.
   */
  @Test(groups = {"edge", "regression"})
  public void testTC037_AuthorizationCheckWithVeryLongSlug() throws Exception {
    createTest(
        "TC-037: Authorization check with very long slug", "Verify 403 for article with long slug");

    // Create article with very long title
    String longTitle =
        "This is a very long article title that will generate a very long slug for testing purposes "
            + System.currentTimeMillis();
    ApiResponse createResponse =
        apiHelper.createArticle(
            longTitle, "Description", "Body", new String[] {"test"}, authorToken);
    assertEquals(createResponse.statusCode, 200, "Article with long title should be created");

    String longSlug = extractSlug(createResponse.body);
    assertNotNull(longSlug, "Slug should be extracted");
    assertTrue(longSlug.length() > 50, "Slug should be long");

    // Non-author tries to edit
    ApiResponse editResponse =
        apiHelper.editArticle(longSlug, "Unauthorized Edit", "Desc", "Body", nonAuthorToken);
    assertEquals(editResponse.statusCode, 403, "Should return 403 for long slug article");
    test.pass("Authorization correctly enforced for article with very long slug");

    // Cleanup
    apiHelper.deleteArticle(longSlug, authorToken);
  }

  /**
   * TC-038: Non-author edit non-existent article. Verifies that attempting to edit a non-existent
   * article returns 404 Not Found (not 403).
   */
  @Test(groups = {"edge", "regression"})
  public void testTC038_NonAuthorEditNonExistentArticle() throws Exception {
    createTest(
        "TC-038: Non-author edit non-existent article", "Verify 404 for non-existent article edit");

    String nonExistentSlug = "this-article-does-not-exist-" + System.currentTimeMillis();

    ApiResponse response =
        apiHelper.editArticle(nonExistentSlug, "Edit Non-Existent", "Desc", "Body", nonAuthorToken);

    // Should return 404 (not found) rather than 403 (forbidden)
    assertEquals(response.statusCode, 404, "Non-existent article should return 404, not 403");
    test.pass("Non-existent article correctly returns 404 Not Found");
  }

  /**
   * TC-039: Non-author delete non-existent article. Verifies that attempting to delete a
   * non-existent article returns 404 Not Found (not 403).
   */
  @Test(groups = {"edge", "regression"})
  public void testTC039_NonAuthorDeleteNonExistentArticle() throws Exception {
    createTest(
        "TC-039: Non-author delete non-existent article",
        "Verify 404 for non-existent article delete");

    String nonExistentSlug = "this-article-also-does-not-exist-" + System.currentTimeMillis();

    ApiResponse response = apiHelper.deleteArticle(nonExistentSlug, nonAuthorToken);

    // Should return 404 (not found) rather than 403 (forbidden)
    assertEquals(response.statusCode, 404, "Non-existent article should return 404, not 403");
    test.pass("Non-existent article delete correctly returns 404 Not Found");
  }

  /**
   * TC-040: Rapid consecutive unauthorized requests. Verifies that multiple rapid unauthorized
   * requests all return 403 consistently.
   */
  @Test(groups = {"edge", "regression"})
  public void testTC040_RapidConsecutiveUnauthorizedRequests() throws Exception {
    createTest(
        "TC-040: Rapid consecutive unauthorized requests",
        "Verify consistent 403 for rapid requests");

    int requestCount = 5;
    int successCount = 0;

    for (int i = 0; i < requestCount; i++) {
      ApiResponse response =
          apiHelper.editArticle(testArticleSlug, "Rapid Edit " + i, "Desc", "Body", nonAuthorToken);
      if (response.statusCode == 403) {
        successCount++;
      }
      test.info("Request " + (i + 1) + " returned status: " + response.statusCode);
    }

    assertEquals(successCount, requestCount, "All rapid requests should return 403 consistently");
    test.pass("All " + requestCount + " rapid unauthorized requests correctly returned 403");
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
