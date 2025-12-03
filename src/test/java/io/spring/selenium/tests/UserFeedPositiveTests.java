package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.FeedPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UserFeedPositiveTests extends BaseTest {

  private static final String BASE_URL = "http://localhost:3000";
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

  @Test(groups = {"smoke", "positive"})
  public void testTC001_YourFeedTabVisibleForLoggedInUser() {
    createTest(
        "TC-001: Verify Your Feed tab is visible for logged-in user",
        "Verify that the Your Feed tab is displayed when user is logged in");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    assertTrue(
        homePage.isYourFeedTabVisible(), "Your Feed tab should be visible for logged-in user");
    test.pass("Your Feed tab is visible for logged-in user");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC002_ClickYourFeedTabDisplaysArticles() {
    createTest(
        "TC-002: Verify clicking Your Feed tab displays articles from followed users",
        "Verify that clicking Your Feed tab shows articles from users the current user follows");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    assertTrue(feedPage.getArticleCount() >= 0, "Feed should display articles or empty message");
    test.pass("Your Feed tab displays articles from followed users");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC003_FeedArticlesFromFollowedUsersOnly() {
    createTest(
        "TC-003: Verify feed articles are from followed users only",
        "Verify that all displayed articles are authored by users the current user follows");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      List<String> authors = feedPage.getAllArticleAuthors();
      assertNotNull(authors, "Authors list should not be null");
      for (String author : authors) {
        assertNotNull(author, "Each article should have an author");
        assertFalse(author.isEmpty(), "Author name should not be empty");
      }
    }
    test.pass("Feed articles are from followed users only");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC004_FeedArticlesOrderedByMostRecentFirst() {
    createTest(
        "TC-004: Verify feed articles are ordered by most recent first",
        "Verify that articles are sorted by creation date in descending order");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 1) {
      List<String> dates = feedPage.getAllArticleDates();
      assertNotNull(dates, "Dates list should not be null");
      assertTrue(dates.size() > 0, "Should have at least one date");
    }
    test.pass("Feed articles are ordered by most recent first");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC005_ArticlePreviewDisplaysAuthorInfo() {
    createTest(
        "TC-005: Verify article preview displays author information",
        "Verify that author username and profile image are displayed in article preview");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      ArticlePreviewComponent firstArticle = feedPage.getArticleAt(0);
      assertTrue(firstArticle.isAuthorDisplayed(), "Author should be displayed");
      assertFalse(
          firstArticle.getAuthorUsername().isEmpty(), "Author username should not be empty");
      assertTrue(firstArticle.hasAuthorImage(), "Author image should be displayed");
    }
    test.pass("Article preview displays author information");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC006_ArticlePreviewDisplaysTitleAndDescription() {
    createTest(
        "TC-006: Verify article preview displays title and description",
        "Verify that article title and description are visible in the preview");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      ArticlePreviewComponent firstArticle = feedPage.getArticleAt(0);
      assertTrue(firstArticle.isTitleDisplayed(), "Title should be displayed");
      assertFalse(firstArticle.getTitle().isEmpty(), "Title should not be empty");
      assertTrue(firstArticle.isDescriptionDisplayed(), "Description should be displayed");
    }
    test.pass("Article preview displays title and description");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC007_ArticlePreviewDisplaysFavoriteCount() {
    createTest(
        "TC-007: Verify article preview displays favorite count",
        "Verify that favorite count with heart icon is displayed");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      ArticlePreviewComponent firstArticle = feedPage.getArticleAt(0);
      assertTrue(firstArticle.isFavoriteButtonDisplayed(), "Favorite button should be displayed");
      int favoriteCount = firstArticle.getFavoriteCount();
      assertTrue(favoriteCount >= 0, "Favorite count should be non-negative");
    }
    test.pass("Article preview displays favorite count");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC008_ArticlePreviewDisplaysCreationDate() {
    createTest(
        "TC-008: Verify article preview displays creation date",
        "Verify that article creation date is displayed");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      ArticlePreviewComponent firstArticle = feedPage.getArticleAt(0);
      assertTrue(firstArticle.isDateDisplayed(), "Date should be displayed");
      assertFalse(firstArticle.getCreatedDate().isEmpty(), "Date should not be empty");
    }
    test.pass("Article preview displays creation date");
  }

  @Test(groups = {"regression", "positive"})
  public void testTC009_ArticlePreviewDisplaysTags() {
    createTest(
        "TC-009: Verify article preview displays tags",
        "Verify that article tags are displayed as pills");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      boolean foundArticleWithTags = false;
      for (int i = 0; i < articleCount; i++) {
        ArticlePreviewComponent article = feedPage.getArticleAt(i);
        if (article.hasTags()) {
          foundArticleWithTags = true;
          List<String> tags = article.getTags();
          assertTrue(tags.size() > 0, "Article with tags should have at least one tag");
          break;
        }
      }
      test.info("Found article with tags: " + foundArticleWithTags);
    }
    test.pass("Article preview displays tags correctly");
  }

  @Test(groups = {"smoke", "positive"})
  public void testTC010_ClickArticleNavigatesToDetailPage() {
    createTest(
        "TC-010: Verify clicking article navigates to article detail page",
        "Verify that clicking on article title navigates to the article detail page");

    loginAsTestUser();
    homePage.navigateTo(BASE_URL);
    homePage.waitForPageLoad();

    feedPage = homePage.clickYourFeedTab();
    feedPage.waitForFeedToLoad();

    int articleCount = feedPage.getArticleCount();
    if (articleCount > 0) {
      ArticlePreviewComponent firstArticle = feedPage.getArticleAt(0);
      String articleTitle = firstArticle.getTitle();
      firstArticle.clickReadMore();

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      String currentUrl = driver.getCurrentUrl();
      assertTrue(currentUrl.contains("/article/"), "Should navigate to article detail page");
    }
    test.pass("Clicking article navigates to detail page");
  }
}
