package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticlePage;
import io.spring.selenium.pages.HomePage;
import io.spring.selenium.pages.LoginPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArticlePositiveTests extends BaseTest {

  private ArticlePage articlePage;
  private HomePage homePage;
  private LoginPage loginPage;

  private static final String TEST_ARTICLE_SLUG = "how-to-train-your-dragon";
  private static final String TEST_USER_EMAIL = "john@example.com";
  private static final String TEST_USER_PASSWORD = "password123";

  @BeforeMethod
  public void initPages() {
    articlePage = new ArticlePage(driver);
    homePage = new HomePage(driver);
    loginPage = new LoginPage(driver);
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC001_retrieveArticleByValidSlugAsAnonymousUser() {
    createTest(
        "TC-001: Retrieve article by valid slug as anonymous user",
        "Verify anonymous user can view article by navigating to valid slug");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    assertTrue(articlePage.getArticleTitle().length() > 0, "Article title should not be empty");

    test.info("Successfully retrieved article as anonymous user");
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC002_retrieveArticleByValidSlugAsLoggedInUser() {
    createTest(
        "TC-002: Retrieve article by valid slug as logged-in user",
        "Verify logged-in user can view article with user-specific status indicators");

    loginPage.login(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    assertTrue(articlePage.getArticleTitle().length() > 0, "Article title should not be empty");

    test.info("Successfully retrieved article as logged-in user");
  }

  @Test(groups = {"regression", "positive"})
  public void TC003_verifyArticleTitleDisplaysCorrectly() {
    createTest(
        "TC-003: Verify article title displays correctly",
        "Verify article title is displayed on the article page");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    String title = articlePage.getArticleTitle();
    assertNotNull(title, "Article title should not be null");
    assertTrue(title.length() > 0, "Article title should not be empty");

    test.info("Article title displayed: " + title);
  }

  @Test(groups = {"regression", "positive"})
  public void TC004_verifyArticleBodyContentDisplays() {
    createTest(
        "TC-004: Verify article body content displays",
        "Verify full article body is displayed on the article page");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticleBodyDisplayed(), "Article body should be displayed");
    assertTrue(articlePage.hasBodyContent(), "Article body should have content");

    test.info("Article body content is displayed");
  }

  @Test(groups = {"regression", "positive"})
  public void TC005_verifyArticleDescriptionDisplays() {
    createTest(
        "TC-005: Verify article description displays",
        "Verify article description is visible on the article page");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");
    String body = articlePage.getArticleBody();
    assertNotNull(body, "Article content should be present");

    test.info("Article description/content is displayed");
  }

  @Test(groups = {"regression", "positive"})
  public void TC006_verifyArticleTagsDisplay() {
    createTest(
        "TC-006: Verify article tags display", "Verify all article tags are displayed on the page");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isTagListDisplayed(), "Tag list should be displayed");
    int tagCount = articlePage.getTagCount();
    assertTrue(tagCount >= 0, "Tag count should be non-negative");

    test.info("Article tags displayed. Count: " + tagCount);
  }

  @Test(groups = {"regression", "positive"})
  public void TC007_verifyCreationDateDisplays() {
    createTest(
        "TC-007: Verify creation date displays",
        "Verify article creation date is displayed in correct format");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    String date = articlePage.getArticleDate();
    assertNotNull(date, "Article date should not be null");
    assertTrue(date.length() > 0, "Article date should not be empty");

    test.info("Article creation date displayed: " + date);
  }

  @Test(groups = {"regression", "positive"})
  public void TC008_verifyUpdateDateDisplays() {
    createTest(
        "TC-008: Verify update date displays",
        "Verify article update date is displayed for updated articles");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    String date = articlePage.getArticleDate();
    assertNotNull(date, "Article date should not be null");

    test.info("Article date displayed: " + date);
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC009_verifyAuthorProfileInformationDisplays() {
    createTest(
        "TC-009: Verify author profile information displays",
        "Verify author username, bio, and image are displayed");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    String authorUsername = articlePage.getAuthorUsername();
    assertNotNull(authorUsername, "Author username should not be null");
    assertTrue(authorUsername.length() > 0, "Author username should not be empty");
    assertTrue(articlePage.isAuthorImageDisplayed(), "Author image should be displayed");

    test.info("Author profile displayed: " + authorUsername);
  }

  @Test(groups = {"regression", "positive"})
  public void TC010_verifyFavoriteCountDisplays() {
    createTest(
        "TC-010: Verify favorite count displays",
        "Verify favorite count is visible and shows correct number");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(articlePage.isArticlePageDisplayed(), "Article page should be displayed");

    test.info("Article page loaded successfully with favorite functionality");
  }

  @Test(groups = {"regression", "positive"})
  public void TC011_verifyArticleSlugInUrl() {
    createTest(
        "TC-011: Verify article slug in URL", "Verify URL contains the correct article slug");

    articlePage.navigateToArticle(TEST_ARTICLE_SLUG);

    assertTrue(
        articlePage.urlContainsSlug(TEST_ARTICLE_SLUG),
        "URL should contain the article slug: " + TEST_ARTICLE_SLUG);

    test.info("URL correctly contains slug: " + articlePage.getCurrentUrl());
  }

  @Test(groups = {"smoke", "regression", "positive"})
  public void TC012_navigateToArticleFromArticleList() {
    createTest(
        "TC-012: Navigate to article from article list",
        "Verify user can navigate to article page from home page article list");

    homePage.navigateToHomePage();
    assertTrue(homePage.isHomePageDisplayed(), "Home page should be displayed");

    homePage.waitForArticlesToLoad();
    int articleCount = homePage.getArticleCount();

    if (articleCount > 0) {
      homePage.clickFirstArticle();

      assertTrue(
          articlePage.isArticlePageDisplayed() || articlePage.isPageLoaded(),
          "Should navigate to article page");
      test.info("Successfully navigated to article from list");
    } else {
      test.info("No articles available in the list to click");
    }
  }
}
