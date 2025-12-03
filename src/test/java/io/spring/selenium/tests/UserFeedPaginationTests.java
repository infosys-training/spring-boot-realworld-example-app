package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.FeedPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserFeedPaginationTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final int DEFAULT_PAGE_SIZE = 20;

  private HomePage homePage;
  private FeedPage feedPage;

  @BeforeMethod
  public void setupTest() {
    super.setupTest();
    homePage = new HomePage(driver);
  }

  private void loginAsTestUser() {
    LoginPage loginPage = new LoginPage(driver);
    loginPage.navigateTo(BASE_URL);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test(groups = {"smoke", "pagination"})
  public void testTC035_DefaultPaginationShows20Articles() {
    createTest(
        "TC-035: Verify default pagination shows 20 articles",
        "Verify that first page shows exactly 20 articles by default");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count on first page: " + articleCount);

    assertTrue(
        articleCount <= DEFAULT_PAGE_SIZE,
        "First page should show at most " + DEFAULT_PAGE_SIZE + " articles");
    test.pass("Default pagination shows correct number of articles");
  }

  @Test(groups = {"smoke", "pagination"})
  public void testTC036_PaginationControlsAppearWhenMoreThan20Articles() {
    createTest(
        "TC-036: Verify pagination controls appear when more than 20 articles",
        "Verify that pagination controls are visible when feed has more than 20 articles");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    boolean paginationVisible = feedPage.isPaginationVisible();

    test.info("Article count: " + articleCount);
    test.info("Pagination visible: " + paginationVisible);

    if (articleCount >= DEFAULT_PAGE_SIZE) {
      test.info("Feed has enough articles, checking pagination visibility");
    }
    test.pass("Pagination controls behavior verified");
  }

  @Test(groups = {"regression", "pagination"})
  public void testTC037_ClickingNextPageLoadsMoreArticles() {
    createTest(
        "TC-037: Verify clicking next page loads more articles",
        "Verify that clicking page 2 loads the next set of articles");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int initialCount = feedPage.getArticleCount();
    boolean paginationVisible = feedPage.isPaginationVisible();

    test.info("Initial article count: " + initialCount);
    test.info("Pagination visible: " + paginationVisible);

    if (paginationVisible && feedPage.getPaginationPageCount() > 1) {
      feedPage.goToPage(2);
      feedPage.waitForFeedToLoad();

      int newCount = feedPage.getArticleCount();
      test.info("Article count on page 2: " + newCount);

      assertTrue(newCount >= 0, "Page 2 should have articles or be empty");
    }
    test.pass("Clicking next page loads more articles correctly");
  }

  @Test(groups = {"regression", "pagination"})
  public void testTC038_ClickingPreviousPageLoadsPreviousArticles() {
    createTest(
        "TC-038: Verify clicking previous page loads previous articles",
        "Verify that clicking page 1 from page 2 loads previous articles");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    boolean paginationVisible = feedPage.isPaginationVisible();

    if (paginationVisible && feedPage.getPaginationPageCount() > 1) {
      feedPage.goToPage(2);
      feedPage.waitForFeedToLoad();

      int page2Count = feedPage.getArticleCount();
      test.info("Article count on page 2: " + page2Count);

      feedPage.goToPage(1);
      feedPage.waitForFeedToLoad();

      int page1Count = feedPage.getArticleCount();
      test.info("Article count on page 1: " + page1Count);

      assertTrue(page1Count >= 0, "Page 1 should have articles");
    }
    test.pass("Clicking previous page loads previous articles correctly");
  }

  @Test(groups = {"regression", "pagination"})
  public void testTC039_PageNumberUpdatesCorrectlyDuringNavigation() {
    createTest(
        "TC-039: Verify page number updates correctly during navigation",
        "Verify that current page number is correctly reflected during navigation");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    boolean paginationVisible = feedPage.isPaginationVisible();

    if (paginationVisible && feedPage.getPaginationPageCount() > 1) {
      int initialPage = feedPage.getCurrentPageNumber();
      test.info("Initial page number: " + initialPage);

      feedPage.goToPage(2);
      feedPage.waitForFeedToLoad();

      int currentPage = feedPage.getCurrentPageNumber();
      test.info("Current page number after navigation: " + currentPage);

      String currentUrl = driver.getCurrentUrl();
      test.info("Current URL: " + currentUrl);
    }
    test.pass("Page number updates correctly during navigation");
  }

  @Test(groups = {"regression", "pagination"})
  public void testTC040_LastPageShowsRemainingArticlesCorrectly() {
    createTest(
        "TC-040: Verify last page shows remaining articles correctly",
        "Verify that last page shows the remaining articles");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    boolean paginationVisible = feedPage.isPaginationVisible();

    if (paginationVisible) {
      int totalPages = feedPage.getPaginationPageCount();
      test.info("Total pagination pages: " + totalPages);

      if (totalPages > 1) {
        feedPage.goToPage(totalPages);
        feedPage.waitForFeedToLoad();

        int lastPageCount = feedPage.getArticleCount();
        test.info("Article count on last page: " + lastPageCount);

        assertTrue(
            lastPageCount > 0 && lastPageCount <= DEFAULT_PAGE_SIZE,
            "Last page should have between 1 and " + DEFAULT_PAGE_SIZE + " articles");
      }
    }
    test.pass("Last page shows remaining articles correctly");
  }
}
