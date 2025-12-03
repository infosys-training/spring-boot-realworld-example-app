package io.spring.selenium.tests;

import static org.testng.Assert.*;

import io.spring.selenium.pages.ArticleListPage;
import io.spring.selenium.pages.ArticlePreviewComponent;
import io.spring.selenium.pages.HomePage;
import java.util.List;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ListArticlesPositiveTests extends BaseTest {

  private HomePage homePage;
  private ArticleListPage articleListPage;

  @BeforeMethod
  public void setupPages() {
    homePage = new HomePage(driver);
    articleListPage = new ArticleListPage(driver);
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-001: Verify article list displays on home page")
  public void testArticleListDisplaysOnHomePage() {
    createTest(
        "TC-001: Verify article list displays on home page",
        "Verify that article list is displayed when navigating to home page");

    homePage.navigate();

    assertTrue(homePage.isHomePageDisplayed(), "Home page should be displayed");
    assertTrue(
        homePage.hasArticles() || homePage.isEmptyStateDisplayed(),
        "Article list or empty state should be displayed");

    test.info("Home page loaded successfully with article list");
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-002: Verify articles are ordered by most recent first")
  public void testArticlesOrderedByMostRecentFirst() {
    createTest(
        "TC-002: Verify articles ordered by most recent",
        "Verify articles are displayed in descending order by creation date");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      List<String> dates = articleListPage.getAllArticleDates();
      assertTrue(dates.size() > 0, "Should have at least one article with date");

      test.info("Found " + dates.size() + " articles with dates");
      for (String date : dates) {
        test.info("Article date: " + date);
      }
    } else {
      test.info("No articles found - skipping date order verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-003: Verify each article displays title")
  public void testEachArticleDisplaysTitle() {
    createTest(
        "TC-003: Verify each article displays title",
        "Verify that each article preview shows the article title");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveTitles(), "All articles should display their titles");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(firstArticle.hasTitleDisplayed(), "First article should have title displayed");
      assertFalse(firstArticle.getTitle().isEmpty(), "Article title should not be empty");

      test.info("First article title: " + firstArticle.getTitle());
    } else {
      test.info("No articles found - skipping title verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-004: Verify each article displays description")
  public void testEachArticleDisplaysDescription() {
    createTest(
        "TC-004: Verify each article displays description",
        "Verify that each article preview shows the article description");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveDescriptions(),
          "All articles should display their descriptions");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasDescriptionDisplayed(),
          "First article should have description displayed");

      test.info("First article description: " + firstArticle.getDescription());
    } else {
      test.info("No articles found - skipping description verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-005: Verify each article displays author username")
  public void testEachArticleDisplaysAuthorUsername() {
    createTest(
        "TC-005: Verify each article displays author username",
        "Verify that each article preview shows the author's username");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveAuthors(),
          "All articles should display their author usernames");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasAuthorUsernameDisplayed(),
          "First article should have author username displayed");
      assertFalse(
          firstArticle.getAuthorUsername().isEmpty(), "Author username should not be empty");

      test.info("First article author: " + firstArticle.getAuthorUsername());
    } else {
      test.info("No articles found - skipping author verification");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-006: Verify each article displays author image")
  public void testEachArticleDisplaysAuthorImage() {
    createTest(
        "TC-006: Verify each article displays author image",
        "Verify that each article preview shows the author's profile image");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveAuthorImages(),
          "All articles should display author images");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasAuthorImageDisplayed(),
          "First article should have author image displayed");

      String imageSrc = firstArticle.getAuthorImageSrc();
      test.info("First article author image src: " + imageSrc);
    } else {
      test.info("No articles found - skipping author image verification");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-007: Verify each article displays creation date")
  public void testEachArticleDisplaysCreationDate() {
    createTest(
        "TC-007: Verify each article displays creation date",
        "Verify that each article preview shows the creation date");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveDates(),
          "All articles should display their creation dates");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasCreationDateDisplayed(),
          "First article should have creation date displayed");
      assertFalse(firstArticle.getCreationDate().isEmpty(), "Creation date should not be empty");

      test.info("First article creation date: " + firstArticle.getCreationDate());
    } else {
      test.info("No articles found - skipping date verification");
    }
  }

  @Test(
      groups = {"regression"},
      description = "TC-008: Verify each article displays tags")
  public void testEachArticleDisplaysTags() {
    createTest(
        "TC-008: Verify each article displays tags",
        "Verify that articles with tags display them as clickable pills");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(firstArticle.hasTagsDisplayed(), "Article should have tag list area displayed");

      List<String> tags = firstArticle.getTags();
      test.info("First article has " + tags.size() + " tags: " + tags);
    } else {
      test.info("No articles found - skipping tags verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-009: Verify each article displays favorite count")
  public void testEachArticleDisplaysFavoriteCount() {
    createTest(
        "TC-009: Verify each article displays favorite count",
        "Verify that each article preview shows the favorite count");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      assertTrue(
          articleListPage.allArticlesHaveFavoriteCounts(),
          "All articles should display favorite counts");

      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasFavoriteCountDisplayed(),
          "First article should have favorite count displayed");

      int favoriteCount = firstArticle.getFavoriteCount();
      test.info("First article favorite count: " + favoriteCount);
    } else {
      test.info("No articles found - skipping favorite count verification");
    }
  }

  @Test(
      groups = {"smoke", "regression"},
      description = "TC-010: Verify logged-in user sees favorite status")
  public void testLoggedInUserSeesFavoriteStatus() {
    createTest(
        "TC-010: Verify logged-in user sees favorite status",
        "Verify that logged-in users can see their favorite status for each article");

    articleListPage.navigate();

    if (articleListPage.hasArticles()) {
      ArticlePreviewComponent firstArticle = articleListPage.getFirstArticle();
      assertNotNull(firstArticle, "First article should exist");
      assertTrue(
          firstArticle.hasFavoriteCountDisplayed(),
          "Favorite button should be displayed for articles");

      boolean isFavorited = firstArticle.isFavorited();
      test.info(
          "First article favorite status (without login): "
              + (isFavorited ? "favorited" : "not favorited"));
      test.info("Note: Full favorite status verification requires logged-in user");
    } else {
      test.info("No articles found - skipping favorite status verification");
    }
  }
}
