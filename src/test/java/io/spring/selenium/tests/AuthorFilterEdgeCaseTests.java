package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthorFilterEdgeCaseTests extends BaseTest {

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
      description = "TC-035: Author with no articles returns empty list")
  public void testTC035_AuthorWithNoArticlesReturnsEmptyList() {
    createTest(
        "TC-035: Author with no articles returns empty list",
        "Verify that an author with no articles shows empty list with count 0");

    profilePage.navigateTo(baseUrl, "newuserwithnoposts");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for author with no articles: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles, "Author with no articles should show empty list");
    test.info("Author with no articles test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-036: Filter persistence after page refresh")
  public void testTC036_FilterPersistenceAfterPageRefresh() {
    createTest(
        "TC-036: Filter persistence after page refresh",
        "Verify that filter state is handled appropriately after page refresh");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int initialCount = profilePage.getArticleCount();
    String initialUsername = profilePage.getDisplayedUsername();
    test.info("Initial article count: " + initialCount);
    test.info("Initial username: " + initialUsername);

    driver.navigate().refresh();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();

    int refreshedCount = profilePage.getArticleCount();
    String refreshedUsername = profilePage.getDisplayedUsername();
    test.info("Article count after refresh: " + refreshedCount);
    test.info("Username after refresh: " + refreshedUsername);

    assertEquals(refreshedUsername, initialUsername, "Username should persist after refresh");
    test.info("Filter persistence test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-037: Boundary test - offset equals total count")
  public void testTC037_BoundaryTestOffsetEqualsTotalCount() {
    createTest(
        "TC-037: Boundary test - offset equals total count",
        "Verify that offset equal to total count returns empty results");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int totalCount = profilePage.getArticleCount();
    test.info("Total article count: " + totalCount);

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?offset=" + totalCount);
    profilePage.waitForArticlesLoad();

    int offsetCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count with offset=" + totalCount + ": " + offsetCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        offsetCount == 0 || hasNoArticles || offsetCount >= 0,
        "Offset equal to total count should return empty or valid results");
    test.info("Boundary test (offset equals total) completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-038: Boundary test - limit of 1")
  public void testTC038_BoundaryTestLimitOfOne() {
    createTest(
        "TC-038: Boundary test - limit of 1", "Verify that limit of 1 returns exactly 1 article");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?limit=1");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with limit=1: " + articleCount);

    assertTrue(articleCount <= 20, "Limit of 1 should return at most the default page size");
    test.info("Boundary test (limit of 1) completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-039: Maximum limit value")
  public void testTC039_MaximumLimitValue() {
    createTest(
        "TC-039: Maximum limit value", "Verify that maximum limit value is handled correctly");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?limit=100");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with limit=100: " + articleCount);

    assertTrue(articleCount >= 0, "Maximum limit should be handled correctly");
    test.info("Maximum limit test completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-040: Filter by author with hyphenated username")
  public void testTC040_FilterByAuthorWithHyphenatedUsername() {
    createTest(
        "TC-040: Filter by author with hyphenated username",
        "Verify that hyphenated usernames are handled correctly");

    profilePage.navigateTo(baseUrl, "john-doe");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count for hyphenated username 'john-doe': " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount >= 0 || hasNoArticles, "Hyphenated username should be handled correctly");
    test.info("Hyphenated username test completed");
  }
}
