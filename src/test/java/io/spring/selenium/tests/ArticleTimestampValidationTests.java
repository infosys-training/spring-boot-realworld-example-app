package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ApiHelper;
import io.spring.selenium.pages.ArticleDetailPage;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ArticleTimestampValidationTests extends BaseTest {

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
      groups = {"validation", "regression", "timestamp"},
      priority = 11)
  public void TC011_verifyCreatedAtTimestampFollowsISO8601Format() {
    createTest(
        "TC-011: Verify createdAt timestamp follows ISO 8601 format",
        "Verify that createdAt timestamp is in ISO 8601 format (YYYY-MM-DDTHH:MM:SS.sssZ)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test ISO8601 CreatedAt " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      assertNotNull(createdAt, "createdAt should not be null");

      test.info("createdAt timestamp: " + createdAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isTimestampInISO8601Format(createdAt),
          "createdAt should follow ISO 8601 format");

      test.pass("createdAt timestamp follows ISO 8601 format");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 12)
  public void TC012_verifyUpdatedAtTimestampFollowsISO8601Format() {
    createTest(
        "TC-012: Verify updatedAt timestamp follows ISO 8601 format",
        "Verify that updatedAt timestamp is in ISO 8601 format (YYYY-MM-DDTHH:MM:SS.sssZ)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test ISO8601 UpdatedAt " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String updatedAt = apiHelper.extractUpdatedAt(response);
      assertNotNull(updatedAt, "updatedAt should not be null");

      test.info("updatedAt timestamp: " + updatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isTimestampInISO8601Format(updatedAt),
          "updatedAt should follow ISO 8601 format");

      test.pass("updatedAt timestamp follows ISO 8601 format");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 13)
  public void TC013_verifyCreatedAtContainsValidYearComponent() {
    createTest(
        "TC-013: Verify createdAt contains valid year component",
        "Verify that createdAt timestamp contains a valid 4-digit year");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Year Validation " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      assertNotNull(createdAt, "createdAt should not be null");

      test.info("createdAt timestamp: " + createdAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidYear(createdAt), "createdAt should contain a valid year (2020-2030)");

      test.pass("createdAt contains valid year component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 14)
  public void TC014_verifyCreatedAtContainsValidMonthComponent() {
    createTest(
        "TC-014: Verify createdAt contains valid month component",
        "Verify that createdAt timestamp contains a valid month (01-12)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Month Validation " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      assertNotNull(createdAt, "createdAt should not be null");

      test.info("createdAt timestamp: " + createdAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidMonth(createdAt), "createdAt should contain a valid month (01-12)");

      test.pass("createdAt contains valid month component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 15)
  public void TC015_verifyCreatedAtContainsValidDayComponent() {
    createTest(
        "TC-015: Verify createdAt contains valid day component",
        "Verify that createdAt timestamp contains a valid day (01-31)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Day Validation " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      assertNotNull(createdAt, "createdAt should not be null");

      test.info("createdAt timestamp: " + createdAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(articlePage.isValidDay(createdAt), "createdAt should contain a valid day (01-31)");

      test.pass("createdAt contains valid day component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 16)
  public void TC016_verifyCreatedAtContainsValidTimeComponent() {
    createTest(
        "TC-016: Verify createdAt contains valid time component",
        "Verify that createdAt timestamp contains valid hours, minutes, and seconds");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test Time Validation " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String createdAt = apiHelper.extractCreatedAt(response);
      assertNotNull(createdAt, "createdAt should not be null");

      test.info("createdAt timestamp: " + createdAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidTime(createdAt), "createdAt should contain valid time (HH:MM:SS)");

      test.pass("createdAt contains valid time component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 17)
  public void TC017_verifyUpdatedAtContainsValidYearComponent() {
    createTest(
        "TC-017: Verify updatedAt contains valid year component",
        "Verify that updatedAt timestamp contains a valid 4-digit year");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test UpdatedAt Year " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String updatedAt = apiHelper.extractUpdatedAt(response);
      assertNotNull(updatedAt, "updatedAt should not be null");

      test.info("updatedAt timestamp: " + updatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidYear(updatedAt), "updatedAt should contain a valid year (2020-2030)");

      test.pass("updatedAt contains valid year component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 18)
  public void TC018_verifyUpdatedAtContainsValidMonthComponent() {
    createTest(
        "TC-018: Verify updatedAt contains valid month component",
        "Verify that updatedAt timestamp contains a valid month (01-12)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test UpdatedAt Month " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String updatedAt = apiHelper.extractUpdatedAt(response);
      assertNotNull(updatedAt, "updatedAt should not be null");

      test.info("updatedAt timestamp: " + updatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidMonth(updatedAt), "updatedAt should contain a valid month (01-12)");

      test.pass("updatedAt contains valid month component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 19)
  public void TC019_verifyUpdatedAtContainsValidDayComponent() {
    createTest(
        "TC-019: Verify updatedAt contains valid day component",
        "Verify that updatedAt timestamp contains a valid day (01-31)");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test UpdatedAt Day " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String updatedAt = apiHelper.extractUpdatedAt(response);
      assertNotNull(updatedAt, "updatedAt should not be null");

      test.info("updatedAt timestamp: " + updatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(articlePage.isValidDay(updatedAt), "updatedAt should contain a valid day (01-31)");

      test.pass("updatedAt contains valid day component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }

  @Test(
      groups = {"validation", "regression", "timestamp"},
      priority = 20)
  public void TC020_verifyUpdatedAtContainsValidTimeComponent() {
    createTest(
        "TC-020: Verify updatedAt contains valid time component",
        "Verify that updatedAt timestamp contains valid hours, minutes, and seconds");

    try {
      apiHelper.login(TEST_EMAIL, TEST_PASSWORD);
      String uniqueTitle = "Test UpdatedAt Time " + System.currentTimeMillis();
      String response = apiHelper.createArticle(uniqueTitle, "Test description", "Test body", null);

      String updatedAt = apiHelper.extractUpdatedAt(response);
      assertNotNull(updatedAt, "updatedAt should not be null");

      test.info("updatedAt timestamp: " + updatedAt);

      ArticleDetailPage articlePage = new ArticleDetailPage(driver);
      assertTrue(
          articlePage.isValidTime(updatedAt), "updatedAt should contain valid time (HH:MM:SS)");

      test.pass("updatedAt contains valid time component");
    } catch (Exception e) {
      test.fail("Test failed: " + e.getMessage());
      fail("Test failed: " + e.getMessage());
    }
  }
}
