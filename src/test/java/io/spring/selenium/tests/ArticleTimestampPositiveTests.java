package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ArticleDetailPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ArticleTimestampPositiveTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  private ApiHelper apiHelper;
  private String testArticleSlug;
  private String originalCreatedAt;
  private String originalUpdatedAt;

  @BeforeClass
  public void setupTestData() {
    apiHelper = new ApiHelper(API_URL);
  }

  @Test(
      groups = {"positive", "smoke", "timestamp"},
      priority = 1)
  public void TC001_verifyCreatedAtTimestampDisplayedOnNewArticle() {
    createTest(
        "TC-001: Verify createdAt timestamp is displayed on new article",
        "Verify that createdAt timestamp is displayed when a new article is created");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article " + System.currentTimeMillis();
      String response =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body content", null);

      testArticleSlug = apiHelper.extractSlug(response);
      originalCreatedAt = apiHelper.extractCreatedAt(response);

      assertNotNull(testArticleSlug, "Article slug should not be null");
      assertNotNull(originalCreatedAt, "createdAt timestamp should not be null");
      assertFalse(originalCreatedAt.isEmpty(), "createdAt timestamp should not be empty");

      test.info("Article created with slug: " + testArticleSlug);
      test.info("createdAt timestamp: " + originalCreatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, testArticleSlug);

      assertTrue(articlePage.isOnArticlePage(), "Should be on article detail page");
      assertTrue(articlePage.hasCreatedAtTimestamp(), "createdAt timestamp should be displayed");

      test.pass("createdAt timestamp is displayed on new article");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "smoke", "timestamp"},
      priority = 2)
  public void TC002_verifyUpdatedAtTimestampDisplayedOnNewArticle() {
    createTest(
        "TC-002: Verify updatedAt timestamp is displayed on new article",
        "Verify that updatedAt timestamp is displayed when a new article is created");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Updated " + System.currentTimeMillis();
      String response =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body content", null);

      String slug = apiHelper.extractSlug(response);
      originalUpdatedAt = apiHelper.extractUpdatedAt(response);

      assertNotNull(slug, "Article slug should not be null");
      assertNotNull(originalUpdatedAt, "updatedAt timestamp should not be null");
      assertFalse(originalUpdatedAt.isEmpty(), "updatedAt timestamp should not be empty");

      test.info("Article created with slug: " + slug);
      test.info("updatedAt timestamp: " + originalUpdatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      assertTrue(articlePage.isOnArticlePage(), "Should be on article detail page");
      assertTrue(articlePage.hasUpdatedAtTimestamp(), "updatedAt timestamp should be displayed");

      test.pass("updatedAt timestamp is displayed on new article");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 3)
  public void TC003_verifyCreatedAtTimestampUnchangedAfterUpdate() {
    createTest(
        "TC-003: Verify createdAt timestamp remains unchanged after article update",
        "Verify that createdAt timestamp does not change when article is updated");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article CreatedAt " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Original description", "Original body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalCreatedAt = apiHelper.extractCreatedAt(createResponse);

      test.info("Original createdAt: " + originalCreatedAt);

      Thread.sleep(1000);

      String updateResponse =
          apiHelper.updateArticle(slug, "Updated Title " + System.currentTimeMillis(), null, null);
      String updatedCreatedAt = apiHelper.extractCreatedAt(updateResponse);

      test.info("Updated createdAt: " + updatedCreatedAt);

      assertEquals(
          updatedCreatedAt, originalCreatedAt, "createdAt should remain unchanged after update");

      test.pass("createdAt timestamp remains unchanged after article update");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 4)
  public void TC004_verifyUpdatedAtChangesAfterTitleUpdate() {
    createTest(
        "TC-004: Verify updatedAt timestamp changes after article title update",
        "Verify that updatedAt timestamp is updated when article title is modified");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Title Update " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("Original updatedAt: " + originalUpdatedAt);

      Thread.sleep(1000);

      String updateResponse =
          apiHelper.updateArticle(slug, "New Title " + System.currentTimeMillis(), null, null);
      String newUpdatedAt = apiHelper.extractUpdatedAt(updateResponse);

      test.info("New updatedAt: " + newUpdatedAt);

      assertNotEquals(
          newUpdatedAt, originalUpdatedAt, "updatedAt should change after title update");

      test.pass("updatedAt timestamp changes after article title update");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 5)
  public void TC005_verifyUpdatedAtChangesAfterBodyUpdate() {
    createTest(
        "TC-005: Verify updatedAt timestamp changes after article body update",
        "Verify that updatedAt timestamp is updated when article body is modified");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Body Update " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Original body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("Original updatedAt: " + originalUpdatedAt);

      Thread.sleep(1000);

      String updateResponse =
          apiHelper.updateArticle(
              slug, null, null, "Updated body content " + System.currentTimeMillis());
      String newUpdatedAt = apiHelper.extractUpdatedAt(updateResponse);

      test.info("New updatedAt: " + newUpdatedAt);

      assertNotEquals(newUpdatedAt, originalUpdatedAt, "updatedAt should change after body update");

      test.pass("updatedAt timestamp changes after article body update");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 6)
  public void TC006_verifyUpdatedAtChangesAfterDescriptionUpdate() {
    createTest(
        "TC-006: Verify updatedAt timestamp changes after article description update",
        "Verify that updatedAt timestamp is updated when article description is modified");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Desc Update " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Original description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String originalUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("Original updatedAt: " + originalUpdatedAt);

      Thread.sleep(1000);

      String updateResponse =
          apiHelper.updateArticle(
              slug, null, "Updated description " + System.currentTimeMillis(), null);
      String newUpdatedAt = apiHelper.extractUpdatedAt(updateResponse);

      test.info("New updatedAt: " + newUpdatedAt);

      assertNotEquals(
          newUpdatedAt, originalUpdatedAt, "updatedAt should change after description update");

      test.pass("updatedAt timestamp changes after article description update");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 7)
  public void TC007_verifyTimestampsVisibleInArticleListView() {
    createTest(
        "TC-007: Verify timestamps are visible in article list view",
        "Verify that timestamps are displayed in the article list on home page");

    try {
      HomePage homePage = new HomePage(driver);
      homePage.navigateTo(BASE_URL);

      assertTrue(homePage.isOnHomePage(), "Should be on home page");

      int articleCount = homePage.getArticleCount();
      assertTrue(articleCount > 0, "Should have at least one article");

      String timestamp = homePage.getArticleTimestamp(0);
      assertNotNull(timestamp, "Timestamp should be visible in article list");
      assertFalse(timestamp.isEmpty(), "Timestamp should not be empty");

      test.info("Found " + articleCount + " articles with timestamps");
      test.info("First article timestamp: " + timestamp);

      test.pass("Timestamps are visible in article list view");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "smoke", "timestamp"},
      priority = 8)
  public void TC008_verifyTimestampsVisibleInArticleDetailView() {
    createTest(
        "TC-008: Verify timestamps are visible in article detail view",
        "Verify that timestamps are displayed on the article detail page");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Detail View " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      assertTrue(articlePage.isOnArticlePage(), "Should be on article detail page");
      assertTrue(
          articlePage.hasCreatedAtTimestamp(),
          "createdAt timestamp should be visible in detail view");
      assertTrue(
          articlePage.hasUpdatedAtTimestamp(),
          "updatedAt timestamp should be visible in detail view");

      String displayedDate = articlePage.getDisplayedDate();
      assertNotNull(displayedDate, "Displayed date should not be null");
      assertFalse(displayedDate.isEmpty(), "Displayed date should not be empty");

      test.info("Displayed date: " + displayedDate);

      test.pass("Timestamps are visible in article detail view");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 9)
  public void TC009_verifyTimestampsVisibleInUserFeed() {
    createTest(
        "TC-009: Verify timestamps are visible in user's articles feed",
        "Verify that timestamps are displayed for articles in user's feed");

    try {
      LoginPage loginPage = new LoginPage(driver);
      loginPage.navigateTo(BASE_URL);
      loginPage.login(TEST_EMAIL, TEST_PASSWORD);

      Thread.sleep(2000);

      HomePage homePage = new HomePage(driver);
      homePage.navigateTo(BASE_URL);

      assertTrue(homePage.isOnHomePage(), "Should be on home page");

      int articleCount = homePage.getArticleCount();
      if (articleCount > 0) {
        String timestamp = homePage.getArticleTimestamp(0);
        assertNotNull(timestamp, "Timestamp should be visible in user feed");
        assertFalse(timestamp.isEmpty(), "Timestamp should not be empty in user feed");
        test.info("Timestamp in user feed: " + timestamp);
      }

      test.pass("Timestamps are visible in user's articles feed");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"positive", "regression", "timestamp"},
      priority = 10)
  public void TC010_verifyTimestampsPersistAfterPageRefresh() {
    createTest(
        "TC-010: Verify timestamps persist after page refresh",
        "Verify that timestamps remain the same after refreshing the page");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Article Refresh " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);
      String expectedCreatedAt = apiHelper.extractCreatedAt(response);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      String beforeRefresh = articlePage.getDisplayedDate();
      test.info("Timestamp before refresh: " + beforeRefresh);

      articlePage.refresh();
      Thread.sleep(1000);

      String afterRefresh = articlePage.getDisplayedDate();
      test.info("Timestamp after refresh: " + afterRefresh);

      assertEquals(afterRefresh, beforeRefresh, "Timestamp should persist after page refresh");

      test.pass("Timestamps persist after page refresh");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }
}
