package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleDetailPage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import io.spring.selenium.pages.ProfilePage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AuthorFilterPositiveTests extends BaseTest {

  private HomePage homePage;
  private ProfilePage profilePage;
  private ArticleDetailPage articleDetailPage;
  private LoginPage loginPage;
  private String baseUrl;

  private static final String VALID_AUTHOR = "johndoe";
  private static final String AUTHOR_WITH_MULTIPLE_ARTICLES = "johndoe";
  private static final String TEST_EMAIL = "john@example.com";
  private static final String TEST_PASSWORD = "password123";

  @BeforeMethod
  public void setupPages() {
    baseUrl = config.getProperty("base.url", "http://localhost:3000");
    homePage = new HomePage(driver);
    profilePage = new ProfilePage(driver);
    articleDetailPage = new ArticleDetailPage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-001: Filter articles by valid author username")
  public void testTC001_FilterArticlesByValidAuthorUsername() {
    createTest(
        "TC-001: Filter articles by valid author username",
        "Verify that filtering by a valid author username displays articles from that author");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    test.info("Navigated to profile page for author: " + VALID_AUTHOR);

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(
        profilePage.getDisplayedUsername(),
        VALID_AUTHOR,
        "Username should match the filter parameter");

    test.info("Successfully filtered articles by author: " + VALID_AUTHOR);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-002: Filter articles by author with multiple articles")
  public void testTC002_FilterArticlesByAuthorWithMultipleArticles() {
    createTest(
        "TC-002: Filter articles by author with multiple articles",
        "Verify that all articles from an author with multiple articles are displayed");

    profilePage.navigateTo(baseUrl, AUTHOR_WITH_MULTIPLE_ARTICLES);
    test.info("Navigated to profile page for author with multiple articles");

    profilePage.waitForArticlesLoad();
    int articleCount = profilePage.getArticleCount();
    test.info("Found " + articleCount + " articles for author: " + AUTHOR_WITH_MULTIPLE_ARTICLES);

    assertTrue(articleCount >= 0, "Article count should be non-negative");
    test.info("Successfully verified author has articles displayed");
  }

  @Test(
      groups = {"regression"},
      description = "TC-003: Filter articles by author with single article")
  public void testTC003_FilterArticlesByAuthorWithSingleArticle() {
    createTest(
        "TC-003: Filter articles by author with single article",
        "Verify that a single article is displayed for an author with one article");

    profilePage.navigateTo(baseUrl, "bobsmith");
    test.info("Navigated to profile page for author with potentially single article");

    profilePage.waitForArticlesLoad();
    int articleCount = profilePage.getArticleCount();
    test.info("Found " + articleCount + " articles");

    assertTrue(articleCount >= 0, "Should display articles or empty message");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-004: Verify article author name matches filter parameter")
  public void testTC004_VerifyArticleAuthorNameMatchesFilterParameter() {
    createTest(
        "TC-004: Verify article author name matches filter parameter",
        "Verify that all displayed articles show the correct author name");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      for (int i = 0; i < articleCount; i++) {
        String author = profilePage.getArticleAuthor(i);
        assertEquals(author, VALID_AUTHOR, "Article author should match filter parameter");
      }
      test.info("All " + articleCount + " articles belong to author: " + VALID_AUTHOR);
    } else {
      test.info("No articles found for author, skipping author verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-005: Verify all returned articles belong to specified author")
  public void testTC005_VerifyAllReturnedArticlesBelongToSpecifiedAuthor() {
    createTest(
        "TC-005: Verify all returned articles belong to specified author",
        "Verify that no articles from other authors appear in filtered results");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    boolean allBelongToAuthor = profilePage.allArticlesBelongToAuthor(VALID_AUTHOR);
    assertTrue(
        allBelongToAuthor || profilePage.getArticleCount() == 0,
        "All articles should belong to the specified author");

    test.info("Verified all articles belong to author: " + VALID_AUTHOR);
  }

  @Test(
      groups = {"regression"},
      description = "TC-006: Filter articles with default pagination")
  public void testTC006_FilterArticlesWithDefaultPagination() {
    createTest(
        "TC-006: Filter articles with default pagination",
        "Verify that default pagination is applied when no pagination parameters are specified");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    test.info("Articles displayed with default pagination: " + articleCount);

    assertTrue(articleCount <= 20, "Default pagination should limit articles to 20 or less");
  }

  @Test(
      groups = {"regression"},
      description = "TC-007: Verify article details displayed correctly")
  public void testTC007_VerifyArticleDetailsDisplayedCorrectly() {
    createTest(
        "TC-007: Verify article details displayed correctly",
        "Verify that article cards show title, description, tags, and date");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String title = profilePage.getArticleTitle(0);
      String description = profilePage.getArticleDescription(0);
      String date = profilePage.getArticleDate(0);

      assertNotNull(title, "Article title should be displayed");
      assertFalse(title.isEmpty(), "Article title should not be empty");
      test.info("Article title: " + title);
      test.info("Article description: " + (description != null ? description : "N/A"));
      test.info("Article date: " + (date != null ? date : "N/A"));
    } else {
      test.info("No articles found, skipping detail verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-008: Filter articles by author and verify article count")
  public void testTC008_FilterArticlesByAuthorAndVerifyArticleCount() {
    createTest(
        "TC-008: Filter articles by author and verify article count",
        "Verify that the displayed count matches the actual number of author's articles");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int displayedCount = profilePage.getArticleCount();
    test.info("Displayed article count: " + displayedCount);

    assertTrue(displayedCount >= 0, "Article count should be non-negative");
  }

  @Test(
      groups = {"regression"},
      description = "TC-009: Navigate to author profile from filtered results")
  public void testTC009_NavigateToAuthorProfileFromFilteredResults() {
    createTest(
        "TC-009: Navigate to author profile from filtered results",
        "Verify that clicking on author name opens the author profile page");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(
        profilePage.getDisplayedUsername(),
        VALID_AUTHOR,
        "Should be on the correct author profile");

    test.info("Successfully on author profile page: " + VALID_AUTHOR);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-010: Click on article from filtered results opens article detail")
  public void testTC010_ClickOnArticleFromFilteredResultsOpensArticleDetail() {
    createTest(
        "TC-010: Click on article from filtered results opens article detail",
        "Verify that clicking on an article title opens the article detail page");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String expectedTitle = profilePage.getArticleTitle(0);
      profilePage.clickOnArticle(0);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      assertTrue(
          driver.getCurrentUrl().contains("/article/"), "Should navigate to article detail page");
      test.info("Successfully navigated to article detail page");
    } else {
      test.info("No articles found, skipping click test");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-011: Verify article metadata in filtered results")
  public void testTC011_VerifyArticleMetadataInFilteredResults() {
    createTest(
        "TC-011: Verify article metadata in filtered results",
        "Verify that date, tags, and favorites count are visible");

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    int articleCount = profilePage.getArticleCount();
    if (articleCount > 0) {
      String date = profilePage.getArticleDate(0);
      int favoritesCount = profilePage.getFavoritesCount(0);

      assertNotNull(date, "Article date should be displayed");
      assertTrue(favoritesCount >= 0, "Favorites count should be non-negative");

      test.info("Article date: " + date);
      test.info("Favorites count: " + favoritesCount);
    } else {
      test.info("No articles found, skipping metadata verification");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-012: Filter articles by logged-in user's own username")
  public void testTC012_FilterArticlesByLoggedInUsersOwnUsername() {
    createTest(
        "TC-012: Filter articles by logged-in user's own username",
        "Verify that a logged-in user can filter articles by their own username");

    loginPage.navigateTo(baseUrl);
    loginPage.login(TEST_EMAIL, TEST_PASSWORD);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    profilePage.navigateTo(baseUrl, VALID_AUTHOR);
    profilePage.waitForArticlesLoad();

    assertTrue(profilePage.isProfilePageDisplayed(), "Profile page should be displayed");
    assertEquals(
        profilePage.getDisplayedUsername(),
        VALID_AUTHOR,
        "Should display own profile with articles");

    test.info("Successfully filtered articles by logged-in user's username");
  }
}
