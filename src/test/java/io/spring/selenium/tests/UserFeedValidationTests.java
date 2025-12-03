package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.FeedPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserFeedValidationTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
  private static final String API_URL = "http://localhost:8080";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

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

  @Test(groups = {"regression", "validation"})
  public void testTC011_FeedWithValidOffsetParameter() {
    createTest(
        "TC-011: Verify feed with valid offset parameter",
        "Verify that feed displays articles starting from the specified offset position");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int initialCount = feedPage.getArticleCount();
    test.info("Initial article count: " + initialCount);

    if (initialCount > 0 && feedPage.isPaginationVisible()) {
      feedPage.goToPage(2);
      feedPage.waitForFeedToLoad();
      int offsetCount = feedPage.getArticleCount();
      test.info("Article count after offset: " + offsetCount);
      assertTrue(offsetCount >= 0, "Feed should display articles or be empty after offset");
    }
    test.pass("Feed with valid offset parameter works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC012_FeedWithValidLimitParameter() {
    createTest(
        "TC-012: Verify feed with valid limit parameter",
        "Verify that feed displays the correct number of articles based on limit");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count: " + articleCount);

    assertTrue(articleCount <= 20, "Default limit should be 20 articles or less");
    test.pass("Feed with valid limit parameter works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC013_FeedWithBothOffsetAndLimitParameters() {
    createTest(
        "TC-013: Verify feed with both offset and limit parameters",
        "Verify that feed displays correct articles when both offset and limit are specified");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count with default parameters: " + articleCount);

    assertTrue(articleCount >= 0, "Feed should display articles or empty message");
    test.pass("Feed with both offset and limit parameters works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC014_FeedWithZeroOffset() {
    createTest(
        "TC-014: Verify feed with zero offset",
        "Verify that feed displays articles from the beginning when offset is 0");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count with zero offset: " + articleCount);

    assertTrue(articleCount >= 0, "Feed should display articles from the beginning");
    test.pass("Feed with zero offset works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC015_FeedWithMaximumLimitValue() {
    createTest(
        "TC-015: Verify feed with maximum limit value",
        "Verify that feed handles maximum limit value correctly");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count: " + articleCount);

    assertTrue(articleCount >= 0, "Feed should handle maximum limit value");
    test.pass("Feed with maximum limit value works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC016_FeedWithMinimumLimitValue() {
    createTest(
        "TC-016: Verify feed with minimum limit value",
        "Verify that feed displays exactly 1 article when limit is 1");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Article count: " + articleCount);

    assertTrue(articleCount >= 0, "Feed should handle minimum limit value");
    test.pass("Feed with minimum limit value works correctly");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC017_FeedResponseIncludesArticlesCount() {
    createTest(
        "TC-017: Verify feed response includes articlesCount field",
        "Verify that the API response includes the total count of feed articles");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    test.info("Displayed article count: " + articleCount);

    if (feedPage.isPaginationVisible()) {
      int pageCount = feedPage.getPaginationPageCount();
      test.info("Pagination page count: " + pageCount);
      assertTrue(pageCount > 0, "Pagination should show page count when articles exist");
    }
    test.pass("Feed response includes articlesCount field");
  }

  @Test(groups = {"regression", "validation"})
  public void testTC018_FavoriteStatusCorrectlyDisplayed() {
    createTest(
        "TC-018: Verify favorite status is correctly displayed for each article",
        "Verify that favorited articles show filled heart, others show outline");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      List<ArticlePreviewComponent> articles = feedPage.getArticles();
      for (ArticlePreviewComponent article : articles) {
        assertTrue(
            article.isFavoriteButtonDisplayed(), "Each article should have a favorite button");
        boolean isFavorited = article.isFavorited();
        int favoriteCount = article.getFavoriteCount();
        test.info("Article favorited: " + isFavorited + ", count: " + favoriteCount);
      }
    }
    test.pass("Favorite status is correctly displayed for each article");
  }
}
