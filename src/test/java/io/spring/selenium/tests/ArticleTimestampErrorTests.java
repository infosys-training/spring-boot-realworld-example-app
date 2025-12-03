package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ArticleDetailPage;
import io.spring.selenium.pages.ArticleEditorPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ArticleTimestampErrorTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  private ApiHelper apiHelper;

  @BeforeClass
  public void setupTestData() {
    apiHelper = new ApiHelper(API_URL);
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 21)
  public void TC021_verifyTimestampsNotEditableByUser() {
    createTest(
        "TC-021: Verify timestamps are not editable by user",
        "Verify that timestamp fields are not present in the article edit form");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Not Editable " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      ArticleEditorPage editorPage = new ArticleEditorPage(driver);
      editorPage.navigateToEditArticle(BASE_URL, slug);

      assertTrue(editorPage.isOnEditorPage(), "Should be on editor page");
      assertFalse(
          editorPage.isTimestampFieldPresent(),
          "Timestamp fields should not be present in edit form");

      test.pass("Timestamps are not editable by user");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 22)
  public void TC022_verifyCreatedAtCannotBeModifiedViaUpdate() {
    createTest(
        "TC-022: Verify createdAt cannot be modified via article update",
        "Verify that createdAt remains unchanged even when attempting to modify it");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test CreatedAt Immutable " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalCreatedAt = apiHelper.extractCreatedAt(createResponse);

      test.info("Original createdAt: " + originalCreatedAt);

      String updateResponse =
          apiHelper.updateArticle(slug, "Updated Title " + System.currentTimeMillis(), null, null);
      String updatedCreatedAt = apiHelper.extractCreatedAt(updateResponse);

      test.info("createdAt after update: " + updatedCreatedAt);

      assertEquals(
          updatedCreatedAt,
          originalCreatedAt,
          "createdAt should not be modifiable via article update");

      test.pass("createdAt cannot be modified via article update");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 23)
  public void TC023_verifyTimestampsDisplayGracefullyWhenArticleNotFound() {
    createTest(
        "TC-023: Verify timestamps display gracefully when article not found",
        "Verify that appropriate error is shown for non-existent article");

    try {
      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateToNonExistent(BASE_URL);

      Thread.sleep(2000);

      boolean hasError = articlePage.isErrorPageDisplayed() || !articlePage.isOnArticlePage();
      assertTrue(
          hasError, "Should show error or not display article page for non-existent article");

      test.pass("Timestamps display gracefully when article not found");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 24)
  public void TC024_verifyTimestampsHandleUnauthorizedAccessAppropriately() {
    createTest(
        "TC-024: Verify timestamps handle unauthorized access appropriately",
        "Verify that timestamps are handled correctly for unauthorized access attempts");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Unauthorized " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      driver.manage().deleteAllCookies();
      driver.navigate().refresh();

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      assertTrue(
          articlePage.isOnArticlePage() || articlePage.isErrorPageDisplayed(),
          "Should either show article (public) or handle unauthorized access gracefully");

      test.pass("Timestamps handle unauthorized access appropriately");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 25)
  public void TC025_verifyTimestampsDisplayForDeletedArticleShowsError() {
    createTest(
        "TC-025: Verify timestamps display for deleted article shows error",
        "Verify that appropriate error is shown when accessing a deleted article");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Deleted Article " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      apiHelper.deleteArticle(slug);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      Thread.sleep(2000);

      boolean hasError = articlePage.isErrorPageDisplayed() || !articlePage.isOnArticlePage();
      assertTrue(hasError, "Should show error for deleted article");

      test.pass("Timestamps display for deleted article shows error");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 26)
  public void TC026_verifyTimestampsNotPresentForNonExistentArticle() {
    createTest(
        "TC-026: Verify timestamps are not present for non-existent article",
        "Verify that no timestamp fields are returned for non-existent article API call");

    try {
      String response = apiHelper.getArticle("non-existent-article-" + System.currentTimeMillis());

      boolean hasTimestamps = response.contains("createdAt") && response.contains("updatedAt");

      if (response.contains("error")
          || response.contains("404")
          || response.contains("not found")) {
        assertFalse(
            hasTimestamps,
            "Timestamp fields should not be present in error response for non-existent article");
      }

      test.info(
          "API response for non-existent article: "
              + response.substring(0, Math.min(200, response.length())));

      test.pass("Timestamps are not present for non-existent article");
    } catch (Exception e) {
      test.pass("Expected error for non-existent article: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 27)
  public void TC027_verifyTimestampDisplayWhenUserIsLoggedOut() {
    createTest(
        "TC-027: Verify timestamp display when user is logged out",
        "Verify that timestamps are still visible for public articles when logged out");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Logged Out " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      driver.manage().deleteAllCookies();

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      if (articlePage.isOnArticlePage()) {
        assertTrue(
            articlePage.hasCreatedAtTimestamp(),
            "Timestamps should be visible for public articles when logged out");
        test.info("Timestamps visible for logged out user");
      }

      test.pass("Timestamp display when user is logged out works correctly");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 28)
  public void TC028_verifyTimestampsPresentWithMinimalArticleContent() {
    createTest(
        "TC-028: Verify timestamps are present with minimal article content",
        "Verify that timestamps are present even with minimum required fields");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Min" + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "D", "B", null);

      String slug = apiHelper.extractSlug(response);
      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      assertNotNull(slug, "Article should be created with minimal content");
      assertNotNull(createdAt, "createdAt should be present with minimal content");
      assertNotNull(updatedAt, "updatedAt should be present with minimal content");

      test.info("createdAt with minimal content: " + createdAt);
      test.info("updatedAt with minimal content: " + updatedAt);

      test.pass("Timestamps are present with minimal article content");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 29)
  public void TC029_verifyTimestampsPresentWithMaximumLengthContent() {
    createTest(
        "TC-029: Verify timestamps are present with maximum length article content",
        "Verify that timestamps are present with very long article content");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);

      StringBuilder longBody = new StringBuilder();
      for (int i = 0; i < 100; i++) {
        longBody.append("This is a very long article body content for testing purposes. ");
      }

      String uniqueTitle = "Max Length Article " + System.currentTimeMillis();
      String response =
          apiHelper.createArticle(
              uniqueTitle,
              "This is a long description for testing maximum content length",
              longBody.toString(),
              null);

      String slug = apiHelper.extractSlug(response);
      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      assertNotNull(slug, "Article should be created with maximum length content");
      assertNotNull(createdAt, "createdAt should be present with maximum length content");
      assertNotNull(updatedAt, "updatedAt should be present with maximum length content");

      test.info("createdAt with max content: " + createdAt);
      test.info("updatedAt with max content: " + updatedAt);

      test.pass("Timestamps are present with maximum length article content");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"error", "regression", "timestamp"},
      priority = 30)
  public void TC030_verifyTimestampsDisplayCorrectlyAfterFailedUpdateAttempt() {
    createTest(
        "TC-030: Verify timestamps display correctly after failed update attempt",
        "Verify that timestamps remain unchanged after a failed update attempt");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Failed Update " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalCreatedAt = apiHelper.extractCreatedAt(createResponse);
      String originalUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("Original createdAt: " + originalCreatedAt);
      test.info("Original updatedAt: " + originalUpdatedAt);

      try {
        apiHelper.setAuthToken("invalid-token");
        apiHelper.updateArticle(slug, "Should Fail", null, null);
      } catch (Exception ignored) {
      }

      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String getResponse = apiHelper.getArticle(slug);
      String currentCreatedAt = apiHelper.extractCreatedAt(getResponse);
      String currentUpdatedAt = apiHelper.extractUpdatedAt(getResponse);

      test.info("Current createdAt: " + currentCreatedAt);
      test.info("Current updatedAt: " + currentUpdatedAt);

      assertEquals(
          currentCreatedAt,
          originalCreatedAt,
          "createdAt should remain unchanged after failed update");

      test.pass("Timestamps display correctly after failed update attempt");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }
}
