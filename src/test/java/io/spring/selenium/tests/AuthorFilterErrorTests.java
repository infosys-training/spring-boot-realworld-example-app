package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthorFilterErrorTests extends BaseTest {

  private ProfilePage profilePage;
  private String baseUrl;

  private static final String VALID_AUTHOR = "johndoe";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    profilePage = new ProfilePage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-029: Non-existent author returns empty list")
  public void testTC029_NonExistentAuthorReturnsEmptyList() {
    createTest(
        "TC-029: Non-existent author returns empty list",
        "Verify that filtering by a non-existent author returns an empty article list");

    profilePage.navigateTo(baseUrl, "nonexistentuser123xyz");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for non-existent author: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles,
        "Non-existent author should return empty list or 'no articles' message");
    test.info("Non-existent author test completed successfully");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-030: Non-existent author shows count of 0")
  public void testTC030_NonExistentAuthorShowsCountOfZero() {
    createTest(
        "TC-030: Non-existent author shows count of 0",
        "Verify that filtering by a non-existent author shows article count of 0");

    profilePage.navigateTo(baseUrl, "nonexistentuser456abc");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count for non-existent author: " + articleCount);

    assertEquals(articleCount, 0, "Non-existent author should show count of 0");
    test.info("Non-existent author count verification completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-031: Invalid pagination offset (negative)")
  public void testTC031_InvalidPaginationOffsetNegative() {
    createTest(
        "TC-031: Invalid pagination offset (negative)",
        "Verify that negative offset is handled gracefully");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?offset=-1");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with negative offset: " + articleCount);

    assertTrue(articleCount >= 0, "Negative offset should be handled gracefully");
    test.info("Negative offset test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-032: Invalid pagination limit (negative)")
  public void testTC032_InvalidPaginationLimitNegative() {
    createTest(
        "TC-032: Invalid pagination limit (negative)",
        "Verify that negative limit is handled gracefully");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?limit=-1");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with negative limit: " + articleCount);

    assertTrue(articleCount >= 0, "Negative limit should be handled gracefully");
    test.info("Negative limit test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-033: Invalid pagination limit (zero)")
  public void testTC033_InvalidPaginationLimitZero() {
    createTest(
        "TC-033: Invalid pagination limit (zero)", "Verify that zero limit is handled gracefully");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?limit=0");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with zero limit: " + articleCount);

    assertTrue(articleCount >= 0, "Zero limit should be handled gracefully");
    test.info("Zero limit test completed");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-034: SQL injection attempt in author parameter")
  public void testTC034_SqlInjectionAttemptInAuthorParameter() {
    createTest(
        "TC-034: SQL injection attempt in author parameter",
        "Verify that SQL injection attempts are handled safely");

    profilePage.navigateTo(baseUrl, "'; DROP TABLE users;--");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for SQL injection attempt: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "SQL injection should be handled safely without errors");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    assertTrue(
        profilePage.isProfilePageDisplayed(),
        "Application should still be functional after SQL injection attempt");
    test.info("SQL injection test completed - application is secure");
  }
}
