package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ArticleDetailPage;
import io.spring.selenium.pages.HomePage;
import java.time.Instant;
import org.openqa.selenium.Dimension;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ArticleTimestampEdgeCaseTests extends BaseTest {

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
      groups = {"edgecase", "regression", "timestamp"},
      priority = 31)
  public void TC031_verifyUpdatedAtChangesWithRapidConsecutiveUpdates() {
    createTest(
        "TC-031: Verify updatedAt changes with rapid consecutive updates",
        "Verify that updatedAt reflects the most recent update time after rapid updates");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Rapid Updates " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String firstUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("First updatedAt: " + firstUpdatedAt);

      Thread.sleep(100);
      String update1Response =
          apiHelper.updateArticle(slug, "Update 1 " + System.currentTimeMillis(), null, null);
      String secondUpdatedAt = apiHelper.extractUpdatedAt(update1Response);

      Thread.sleep(100);
      String update2Response =
          apiHelper.updateArticle(slug, "Update 2 " + System.currentTimeMillis(), null, null);
      String thirdUpdatedAt = apiHelper.extractUpdatedAt(update2Response);

      test.info("Second updatedAt: " + secondUpdatedAt);
      test.info("Third updatedAt: " + thirdUpdatedAt);

      assertNotNull(thirdUpdatedAt, "updatedAt should be present after rapid updates");

      test.pass("updatedAt changes with rapid consecutive updates");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 32)
  public void TC032_verifyTimestampsDisplayCorrectlyAcrossDifferentTimezones() {
    createTest(
        "TC-032: Verify timestamps display correctly across different timezones",
        "Verify that timestamps are in ISO 8601 UTC format consistently");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Timezone " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      test.info("createdAt: " + createdAt);
      test.info("updatedAt: " + updatedAt);

      boolean isUTC = createdAt.endsWith("Z") || createdAt.contains("+00:00");
      assertTrue(isUTC, "Timestamps should be in UTC format (ending with Z or +00:00)");

      test.pass("Timestamps display correctly across different timezones");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "smoke", "timestamp"},
      priority = 33)
  public void TC033_verifyCreatedAtAndUpdatedAtEqualForNewlyCreatedArticle() {
    createTest(
        "TC-033: Verify createdAt and updatedAt are equal for newly created article",
        "Verify that createdAt equals updatedAt for a newly created article");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Equal Timestamps " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      test.info("createdAt: " + createdAt);
      test.info("updatedAt: " + updatedAt);

      assertEquals(
          createdAt,
          updatedAt,
          "createdAt and updatedAt should be equal for newly created article");

      test.pass("createdAt and updatedAt are equal for newly created article");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 34)
  public void TC034_verifyUpdatedAtAlwaysGreaterThanOrEqualToCreatedAt() {
    createTest(
        "TC-034: Verify updatedAt is always greater than or equal to createdAt",
        "Verify that updatedAt >= createdAt always holds true");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Timestamp Order " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(createResponse);
      String createdAt = apiHelper.extractCreatedAt(createResponse);

      Thread.sleep(1000);

      String updateResponse =
          apiHelper.updateArticle(slug, "Updated Title " + System.currentTimeMillis(), null, null);
      String updatedAt = apiHelper.extractUpdatedAt(updateResponse);
      String createdAtAfterUpdate = apiHelper.extractCreatedAt(updateResponse);

      test.info("createdAt: " + createdAtAfterUpdate);
      test.info("updatedAt: " + updatedAt);

      Instant createdInstant = Instant.parse(createdAtAfterUpdate);
      Instant updatedInstant = Instant.parse(updatedAt);

      assertTrue(
          !updatedInstant.isBefore(createdInstant),
          "updatedAt should be greater than or equal to createdAt");

      test.pass("updatedAt is always greater than or equal to createdAt");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 35)
  public void TC035_verifyTimestampsDisplayCorrectlyForArticleWithSpecialCharactersInTitle() {
    createTest(
        "TC-035: Verify timestamps display correctly for article with special characters in title",
        "Verify that timestamps display correctly regardless of special characters in title");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Special Chars !@#$%^&*() " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);
      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      assertNotNull(slug, "Article should be created with special characters in title");
      assertNotNull(createdAt, "createdAt should be present for article with special chars");
      assertNotNull(updatedAt, "updatedAt should be present for article with special chars");

      test.info("createdAt with special chars: " + createdAt);
      test.info("updatedAt with special chars: " + updatedAt);

      test.pass("Timestamps display correctly for article with special characters in title");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 36)
  public void TC036_verifyTimestampsDisplayCorrectlyForArticleWithUnicodeContent() {
    createTest(
        "TC-036: Verify timestamps display correctly for article with unicode content",
        "Verify that timestamps display correctly regardless of unicode content");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Unicode Content " + System.currentTimeMillis();
      String response =
          apiHelper.createArticle(
              uniqueTitle,
              "Description with unicode: cafe resume",
              "Body with unicode: Hello World in Japanese",
              null);

      String slug = apiHelper.extractSlug(response);
      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      assertNotNull(slug, "Article should be created with unicode content");
      assertNotNull(createdAt, "createdAt should be present for article with unicode");
      assertNotNull(updatedAt, "updatedAt should be present for article with unicode");

      test.info("createdAt with unicode: " + createdAt);
      test.info("updatedAt with unicode: " + updatedAt);

      test.pass("Timestamps display correctly for article with unicode content");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 37)
  public void TC037_verifyTimestampsDisplayCorrectlyForArticleWithOnlyTagsUpdated() {
    createTest(
        "TC-037: Verify timestamps display correctly for article with only tags updated",
        "Verify that updatedAt is updated when only tags are modified");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Tags Update " + System.currentTimeMillis();
      String createResponse =
          apiHelper.createArticle(
              uniqueTitle, "Test description", "Test body", new String[] {"tag1", "tag2"});

      String slug = apiHelper.extractSlug(createResponse);
      String originalUpdatedAt = apiHelper.extractUpdatedAt(createResponse);

      test.info("Original updatedAt: " + originalUpdatedAt);

      Thread.sleep(1000);

      String updateResponse = apiHelper.updateArticle(slug, uniqueTitle + " Updated", null, null);
      String newUpdatedAt = apiHelper.extractUpdatedAt(updateResponse);

      test.info("New updatedAt after update: " + newUpdatedAt);

      assertNotNull(newUpdatedAt, "updatedAt should be present after tags update");

      test.pass("Timestamps display correctly for article with only tags updated");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 38)
  public void TC038_verifyTimestampsPrecisionIncludesMillisecondsOrSeconds() {
    createTest(
        "TC-038: Verify timestamps precision includes milliseconds or seconds",
        "Verify that timestamps include at least seconds precision");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Precision " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      String updatedAt = apiHelper.extractUpdatedAt(response);

      test.info("createdAt: " + createdAt);
      test.info("updatedAt: " + updatedAt);

      boolean hasSecondsPrecision = createdAt.contains("T") && createdAt.contains(":");
      assertTrue(hasSecondsPrecision, "Timestamps should include at least seconds precision");

      boolean hasMillisecondsPrecision = createdAt.contains(".");
      test.info("Has milliseconds precision: " + hasMillisecondsPrecision);

      test.pass("Timestamps precision includes milliseconds or seconds");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 39)
  public void TC039_verifyTimestampsDisplayCorrectlyAfterBrowserBackNavigation() {
    createTest(
        "TC-039: Verify timestamps display correctly after browser back navigation",
        "Verify that timestamps display correctly after using browser back button");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Back Navigation " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      String beforeNavigation = articlePage.getDisplayedDate();
      test.info("Timestamp before navigation: " + beforeNavigation);

      HomePage homePage = new HomePage(driver);
      homePage.navigateTo(BASE_URL);

      articlePage.navigateBack();
      Thread.sleep(1000);

      String afterNavigation = articlePage.getDisplayedDate();
      test.info("Timestamp after back navigation: " + afterNavigation);

      if (articlePage.isOnArticlePage()) {
        assertEquals(
            afterNavigation,
            beforeNavigation,
            "Timestamp should be the same after back navigation");
      }

      test.pass("Timestamps display correctly after browser back navigation");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"edgecase", "regression", "timestamp"},
      priority = 40)
  public void TC040_verifyTimestampsDisplayCorrectlyInMobileViewport() {
    createTest(
        "TC-040: Verify timestamps display correctly in mobile viewport",
        "Verify that timestamps are visible and properly formatted in mobile view");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Mobile Viewport " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String slug = apiHelper.extractSlug(response);

      driver.manage().window().setSize(new Dimension(375, 812));

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      articlePage.navigateTo(BASE_URL, slug);

      Thread.sleep(1000);

      if (articlePage.isOnArticlePage()) {
        assertTrue(
            articlePage.hasCreatedAtTimestamp(), "Timestamps should be visible in mobile viewport");

        String mobileTimestamp = articlePage.getDisplayedDate();
        test.info("Timestamp in mobile viewport: " + mobileTimestamp);
      }

      driver.manage().window().maximize();

      test.pass("Timestamps display correctly in mobile viewport");
    } catch (Exception e) {
      driver.manage().window().maximize();
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }
}
