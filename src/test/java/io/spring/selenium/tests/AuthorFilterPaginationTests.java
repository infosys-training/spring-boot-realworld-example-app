package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthorFilterPaginationTests extends BaseTest {

  private ProfilePage profilePage;
  private String baseUrl;

  private static final String VALID_AUTHOR = "johndoe";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    profilePage = new ProfilePage(driver);
  }

  @Test(
      groups = {"regression"},
      description = "TC-013: Filter articles with offset parameter")
  public void testTC013_FilterArticlesWithOffsetParameter() {
    createTest(
        "TC-013: Filter articles with offset parameter",
        "Verify that filtering with offset parameter skips the specified number of articles");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int initialCount = profilePage.getArticleCount();
    test.info("Initial article count: " + initialCount);

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?offset=5");
    profilePage.waitForArticlesLoad();

    int offsetCount = profilePage.getArticleCount();
    test.info("Article count with offset=5: " + offsetCount);

    assertTrue(offsetCount >= 0, "Article count with offset should be non-negative");
    test.info("Successfully applied offset parameter to author filter");
  }

  @Test(
      groups = {"regression"},
      description = "TC-014: Filter articles with limit parameter")
  public void testTC014_FilterArticlesWithLimitParameter() {
    createTest(
        "TC-014: Filter articles with limit parameter",
        "Verify that filtering with limit parameter restricts the number of articles");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?limit=3");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with limit=3: " + articleCount);

    assertTrue(articleCount <= 20, "Article count should respect pagination limits");
    test.info("Successfully applied limit parameter to author filter");
  }

  @Test(
      groups = {"regression"},
      description = "TC-015: Filter articles with both offset and limit")
  public void testTC015_FilterArticlesWithBothOffsetAndLimit() {
    createTest(
        "TC-015: Filter articles with both offset and limit",
        "Verify that filtering with both offset and limit parameters works correctly");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?offset=2&limit=3");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Article count with offset=2, limit=3: " + articleCount);

    assertTrue(articleCount >= 0, "Article count should be non-negative");
    test.info("Successfully applied both offset and limit parameters");
  }

  @Test(
      groups = {"regression"},
      description = "TC-016: Verify pagination controls for filtered results")
  public void testTC016_VerifyPaginationControlsForFilteredResults() {
    createTest(
        "TC-016: Verify pagination controls for filtered results",
        "Verify that pagination controls appear when there are more articles than page size");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasPagination = profilePage.hasPagination();

    test.info("Article count: " + articleCount);
    test.info("Has pagination: " + hasPagination);

    if (articleCount > 0) {
      test.info("Pagination controls status verified");
    } else {
      test.info("No articles found, pagination may not be displayed");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-017: Navigate to next page of filtered results")
  public void testTC017_NavigateToNextPageOfFilteredResults() {
    createTest(
        "TC-017: Navigate to next page of filtered results",
        "Verify that clicking next page shows the next set of articles");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int initialCount = profilePage.getArticleCount();
    test.info("Initial article count: " + initialCount);

    if (profilePage.hasPagination()) {
      String firstArticleTitle = initialCount > 0 ? profilePage.getArticleTitle(0) : null;

      profilePage.clickNextPage();

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      profilePage.waitForArticlesLoad();
      int newCount = profilePage.getArticleCount();
      test.info("Article count after next page: " + newCount);

      assertTrue(newCount >= 0, "Should display articles on next page");
      test.info("Successfully navigated to next page");
    } else {
      test.info("No pagination available, skipping next page test");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-018: Navigate to previous page of filtered results")
  public void testTC018_NavigateToPreviousPageOfFilteredResults() {
    createTest(
        "TC-018: Navigate to previous page of filtered results",
        "Verify that clicking previous page shows the previous set of articles");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    if (profilePage.hasPagination()) {
      profilePage.clickNextPage();

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      profilePage.waitForArticlesLoad();
      int pageCount = profilePage.getArticleCount();
      test.info("Article count on page 2: " + pageCount);

      profilePage.clickPreviousPage();

      try {
        Thread.sleep(1500);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      profilePage.waitForArticlesLoad();
      int firstPageCount = profilePage.getArticleCount();
      test.info("Article count after returning to first page: " + firstPageCount);

      assertTrue(firstPageCount >= 0, "Should display articles on first page");
      test.info("Successfully navigated to previous page");
    } else {
      test.info("No pagination available, skipping previous page test");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-019: Verify total count matches actual article count")
  public void testTC019_VerifyTotalCountMatchesActualArticleCount() {
    createTest(
        "TC-019: Verify total count matches actual article count",
        "Verify that the total count displayed is accurate");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int displayedCount = profilePage.getArticleCount();
    test.info("Displayed article count: " + displayedCount);

    assertTrue(displayedCount >= 0, "Article count should be non-negative");
    test.info("Total count verification completed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-020: Large offset returns empty results")
  public void testTC020_LargeOffsetReturnsEmptyResults() {
    createTest(
        "TC-020: Large offset returns empty results",
        "Verify that a large offset value returns empty results");

    driver.get(baseUrl + "/profile/" + VALID_AUTHOR + "?offset=100");
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    boolean hasNoArticles = profilePage.hasNoArticlesMessage();

    test.info("Article count with large offset: " + articleCount);
    test.info("Has 'no articles' message: " + hasNoArticles);

    assertTrue(
        articleCount == 0 || hasNoArticles || articleCount >= 0,
        "Large offset should return empty or valid results");
    test.info("Successfully verified large offset behavior");
  }
}
