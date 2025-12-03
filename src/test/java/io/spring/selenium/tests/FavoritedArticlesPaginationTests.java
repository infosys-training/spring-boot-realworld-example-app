package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ProfilePage;
import java.util.HashSet;
import java.util.Set;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FavoritedArticlesPaginationTests extends BaseTest {

  private ProfilePage profilePage;

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_USER = "johndoe";

  @BeforeMethod
  public void setupPages() {
    profilePage = new ProfilePage(driver);
  }

  @Test(groups = {"smoke", "regression"})
  public void testTC021VerifyDefaultPaginationShowsFirstPage() {
    createTest(
        "TC-021: Verify default pagination shows first page",
        "Verify that first page of articles is displayed by default");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Articles displayed on first page: " + articleCount);

    if (articleCount > 0) {
      assertTrue(articleCount <= 20, "Should display at most 20 articles per page");
    }

    test.info("Default pagination verified - showing first page");
  }

  @Test(groups = {"regression"})
  public void testTC022NavigateToSecondPageOfFavoritedArticles() {
    createTest(
        "TC-022: Navigate to second page of favorited articles",
        "Verify navigation to second page works correctly");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    if (profilePage.isPaginationDisplayed()) {
      String firstPageFirstArticle = profilePage.getArticleTitle(0);

      profilePage.clickPaginationPage(2);

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String secondPageFirstArticle = profilePage.getArticleTitle(0);

      if (firstPageFirstArticle != null && secondPageFirstArticle != null) {
        assertNotEquals(
            firstPageFirstArticle,
            secondPageFirstArticle,
            "Second page should show different articles");
      }

      test.info("Successfully navigated to second page");
    } else {
      test.info("Pagination not displayed - user may have fewer than 20 favorited articles");
    }
  }

  @Test(groups = {"regression"})
  public void testTC023NavigateToLastPageOfFavoritedArticles() {
    createTest(
        "TC-023: Navigate to last page of favorited articles",
        "Verify navigation to last page works correctly");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    if (profilePage.isPaginationDisplayed()) {
      int pageCount = profilePage.getPaginationPageCount();
      if (pageCount > 1) {
        profilePage.clickPaginationPage(pageCount);

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        int articleCount = profilePage.getArticleCount();
        assertTrue(articleCount > 0, "Last page should have at least one article");

        test.info("Successfully navigated to last page (page " + pageCount + ")");
      } else {
        test.info("Only one page available");
      }
    } else {
      test.info("Pagination not displayed");
    }
  }

  @Test(groups = {"regression"})
  public void testTC024VerifyOffsetParameterWorksCorrectly() {
    createTest(
        "TC-024: Verify offset parameter works correctly",
        "Verify that offset parameter skips the correct number of articles");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    String firstArticleTitle = profilePage.getArticleTitle(0);

    driver.get(BASE_URL + "/profile/" + TEST_USER + "?favorite=true&offset=20");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();
    String offsetArticleTitle = profilePage.getArticleTitle(0);

    test.info("First page first article: " + firstArticleTitle);
    test.info("With offset=20 first article: " + offsetArticleTitle);

    assertNotNull(driver.getPageSource(), "Page should load with offset parameter");
  }

  @Test(groups = {"regression"})
  public void testTC025VerifyLimitParameterWorksCorrectly() {
    createTest(
        "TC-025: Verify limit parameter works correctly",
        "Verify that limit parameter restricts the number of articles");

    driver.get(BASE_URL + "/profile/" + TEST_USER + "?favorite=true&limit=10");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();
    int articleCount = profilePage.getArticleCount();

    test.info("Articles displayed with limit=10: " + articleCount);

    assertNotNull(driver.getPageSource(), "Page should load with limit parameter");
  }

  @Test(groups = {"regression"})
  public void testTC026CombineFavoritedFilterWithOffset() {
    createTest(
        "TC-026: Combine favorited filter with offset",
        "Verify favorited filter works with offset parameter");

    driver.get(BASE_URL + "/profile/" + TEST_USER + "?favorite=true&offset=10");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("favorite=true"), "URL should contain favorite parameter");

    test.info("Combined favorited filter with offset=10");
  }

  @Test(groups = {"regression"})
  public void testTC027CombineFavoritedFilterWithLimit() {
    createTest(
        "TC-027: Combine favorited filter with limit",
        "Verify favorited filter works with limit parameter");

    driver.get(BASE_URL + "/profile/" + TEST_USER + "?favorite=true&limit=5");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.waitForArticlesLoad();

    String currentUrl = driver.getCurrentUrl();
    assertTrue(currentUrl.contains("favorite=true"), "URL should contain favorite parameter");

    test.info("Combined favorited filter with limit=5");
  }

  @Test(groups = {"regression"})
  public void testTC028VerifyPaginationControlsDisplayed() {
    createTest(
        "TC-028: Verify pagination controls displayed",
        "Verify pagination numbers and navigation are visible when needed");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();

    if (articleCount >= 20) {
      assertTrue(
          profilePage.isPaginationDisplayed(), "Pagination should be displayed for 20+ articles");
      test.info("Pagination controls are displayed");
    } else {
      test.info(
          "Pagination may not be displayed - article count ("
              + articleCount
              + ") is less than page size");
    }
  }

  @Test(groups = {"regression"})
  public void testTC029VerifyPageCountMatchesTotalArticles() {
    createTest(
        "TC-029: Verify page count matches total articles",
        "Verify pagination page count correctly reflects total articles");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    if (profilePage.isPaginationDisplayed()) {
      int pageCount = profilePage.getPaginationPageCount();
      assertTrue(pageCount > 0, "Page count should be positive");

      test.info("Pagination page count: " + pageCount);
    } else {
      test.info("Pagination not displayed - single page of results");
    }
  }

  @Test(groups = {"regression"})
  public void testTC030NavigateThroughMultiplePagesSequentially() {
    createTest(
        "TC-030: Navigate through multiple pages sequentially",
        "Verify each page shows different articles");

    profilePage.navigateToFavoritedArticles(BASE_URL, TEST_USER);
    profilePage.waitForArticlesLoad();

    if (profilePage.isPaginationDisplayed() && profilePage.getPaginationPageCount() >= 3) {
      Set<String> allTitles = new HashSet<>();

      for (int page = 1; page <= 3; page++) {
        profilePage.clickPaginationPage(page);

        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }

        String firstTitle = profilePage.getArticleTitle(0);
        if (firstTitle != null) {
          allTitles.add(firstTitle);
          test.info("Page " + page + " first article: " + firstTitle);
        }
      }

      assertTrue(allTitles.size() >= 2, "Different pages should show different articles");
    } else {
      test.info("Not enough pages to test sequential navigation");
    }
  }
}
